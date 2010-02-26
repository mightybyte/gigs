/*
 * Connects your local bot to a remote map server for one game.
 * The map server connects random available bots on random maps,
 * updating the map and enforcing the rules.
 *
 * gcc -o tcptron tcptron.c
 * ./tcptron 213.3.30.106 9999 ./MyTronBot
 *
 * Copyright (c) 2010 Daniel Hartmeier <daniel@benzedrine.cx>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *    - Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

#include <arpa/inet.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define SERVER 0
#define BOT 1
#define USER 2

static int
tcp_connect(const char *host, unsigned port)
{
  int fd;
  struct sockaddr_in sa;

  if ((fd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
    printf("socket: %s\n", strerror(errno));
    return (-1);
  }
  memset(&sa, 0, sizeof(sa));
  sa.sin_family = AF_INET;
  sa.sin_addr.s_addr = inet_addr(host);
  sa.sin_port = htons(port);
  if (connect(fd, (struct sockaddr *)&sa, sizeof(sa))) {
    printf("connect: %s\n", strerror(errno));
    close(fd);
    return (-1);
  }
  return (fd);
}

static pid_t
bpopen(char *cmd, int *fdr, int *fdw)
{
  int p2c[2], c2p[2];
  pid_t pid;
  char *argv[] = { NULL, NULL };

  argv[0] = cmd;
  if (pipe(p2c) || pipe(c2p)) {
    printf("pipe: %s\n", strerror(errno));
    return (0);
  }
  if ((pid = fork()) < 0) {
    printf("fork: %s\n", strerror(errno));
    return (0);
  }
  if (!pid) {
    close(c2p[0]);
    close(p2c[1]);
    dup2(c2p[1], STDOUT_FILENO);
    dup2(p2c[0], STDIN_FILENO);
    execv(argv[0], argv);
    fprintf(stderr, "execv: %s: failed\n", argv[0]);
    exit(1);
  }
  close(c2p[1]);
  close(p2c[0]);
  *fdw = p2c[1];
  *fdr = c2p[0];
  return (pid);
}

int main(int argc, char *argv[])
{
  int in_fds[3] = { -1, -1, -1 };
  int fdToServer;
  int fdToBot;
  int fdToUser = fileno(stdout);;
  pid_t child;
  int i, j, r, len;
  fd_set read_fds;
  struct timeval tv;
  char buf[1024];
  int state = 0;

  if (argc != 4) {
    printf("usage: %s host port command\n", argv[0]);
    return (1);
  }

  in_fds[USER] = fileno(stdin);

  if (!(child = bpopen(argv[3], &in_fds[BOT], &fdToBot)))
    goto done;
  sleep(3); /* allow child to properly startup... */

  if ((in_fds[SERVER] = tcp_connect(argv[1], atoi(argv[2]))) < 0)
    goto done;
  fdToServer = in_fds[SERVER];

  printf("connected to %s:%s, waiting for game\n", argv[1], argv[2]);
  int maxFd = -1;
  for ( i = 0; i < 3; i++ ) {
    if ( in_fds[i] > maxFd ) maxFd = in_fds[i];
  }
  while (1) {
    FD_ZERO(&read_fds);
    FD_SET(in_fds[SERVER], &read_fds);
    FD_SET(in_fds[BOT], &read_fds);
    FD_SET(in_fds[USER], &read_fds);
    tv.tv_sec = 0;
    tv.tv_usec = 1000;
    r = select(maxFd, &read_fds, NULL, NULL, &tv);
    if (r < 0) {
      if (errno != EINTR) {
        printf("select: %s\n", strerror(errno));
        goto done;
      }
      continue;
    }
    if (r == 0)
      continue;
    
    for (i = 0; i < 3; ++i) {
      if (!FD_ISSET(in_fds[i], &read_fds))
        continue;
      len = read(in_fds[i], buf, sizeof(buf) - 1);
      if (len < 0) {
        if (errno != EINTR) {
          printf("read: %s\n", strerror(errno));
          goto done;
        }
        continue;
      }
      if (len == 0)
        goto done;
      buf[len] = 0;

      if ( i != USER ) {
        write(fdToUser, buf, len);
      }

      if ( i == SERVER ) {
        for ( j = 0; j < len; j++ ) {
          if ( state == 0 ) {
            if ( buf[j] == 10 ) state++;
          } else if ( state >= 1 && state <= 3 ) {
            if ( buf[j] == '~' ) state++;
          } else if ( state == 4 ) {
            if ( buf[j] == 10 ) state=0;
            write(fdToBot, buf+j, 1);
          }
        }
      } else {
        write(fdToServer, buf, len);
      }
    }
  }
  
done:
  for (i = 0; i < 3; ++i)
    if (in_fds[i] != -1)
      close(in_fds[i]);
  if (child)
    wait(NULL);
  return (0);
}

