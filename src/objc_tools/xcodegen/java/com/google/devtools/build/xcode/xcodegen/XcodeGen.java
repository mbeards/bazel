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

package com.google.devtools.build.xcode.xcodegen;

import com.google.devtools.build.xcode.xcodegen.proto.XcodeGenProtos.Control;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.Options;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry-point for the command-line Xcode project generator.
 */
public class XcodeGen {
  /**
   * Options for {@link XcodeGen}.
   */
  public static class XcodeGenOptions extends OptionsBase {
    @Option(
        name = "root",
        help = "The root path, from which all other paths are specified relative to.",
        defaultValue = ".")
    public String root;

    @Option(
        name = "control",
        help = "Path to a control file, which contains only a binary serialized instance of "
            + "the Control protocol buffer. Required.",
        defaultValue = "null")
    public String control;
  }

  public static void main(String[] args) throws IOException, OptionsParsingException {
    OptionsParser parser = OptionsParser.newOptionsParser(XcodeGenOptions.class);
    parser.parse(args);
    XcodeGenOptions options = parser.getOptions(XcodeGenOptions.class);
    if (options.control == null) {
      throw new IllegalArgumentException("--control must be specified\n"
          + Options.getUsage(XcodeGenOptions.class));
    }
    FileSystem fileSystem = FileSystems.getDefault();
    Path rootPath = fileSystem.getPath(options.root);

    Control controlPb;
    try (InputStream in = Files.newInputStream(fileSystem.getPath(options.control))) {
      controlPb = Control.parseFrom(in);
    }

    try (OutputStream out = Files.newOutputStream(rootPath.resolve(controlPb.getPbxproj()))) {
      XcodeprojGeneration.write(out,
          XcodeprojGeneration.xcodeproj(rootPath, controlPb, new GrouperImpl(fileSystem)));
    }
  }
}