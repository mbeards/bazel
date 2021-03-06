// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "src/main/native/unix_jni.h"

#include <string.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/xattr.h>

#include <string>

std::string ErrorMessage(int error_number) {
  char buf[1024] = "";

  // In its infinite wisdom, GNU libc defines strerror_r with extended
  // functionality which is not compatible with not the
  // SUSv3-conformant one which returns an error code; see DESCRIPTION
  // at strerror(1).
  return std::string(strerror_r(error_number, buf, sizeof buf));
}

int portable_fstatat(int dirfd, char *name, struct stat *statbuf, int flags) {
  return fstatat(dirfd, name, statbuf, flags);
}

int StatSeconds(const struct stat &statbuf, StatTimes t) {
  switch (t) {
    case STAT_ATIME:
      return statbuf.st_atim.tv_sec;
    case STAT_CTIME:
      return statbuf.st_ctim.tv_sec;
    case STAT_MTIME:
      return statbuf.st_mtim.tv_sec;
    default:
      CHECK(false);
  }
  return 0;
}

int StatNanoSeconds(const struct stat &statbuf, StatTimes t) {
  switch (t) {
    case STAT_ATIME:
      return statbuf.st_atim.tv_nsec;
    case STAT_CTIME:
      return statbuf.st_ctim.tv_nsec;
    case STAT_MTIME:
      return statbuf.st_mtim.tv_nsec;
    default:
      CHECK(false);
  }
  return 0;
}

ssize_t portable_getxattr(const char *path, const char *name, void *value,
                          size_t size) {
  return ::getxattr(path, name, value, size);
}

ssize_t portable_lgetxattr(const char *path, const char *name, void *value,
                           size_t size) {
  return ::lgetxattr(path, name, value, size);
}
