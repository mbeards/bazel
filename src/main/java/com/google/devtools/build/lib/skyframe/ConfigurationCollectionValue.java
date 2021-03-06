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
package com.google.devtools.build.lib.skyframe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.analysis.config.BuildConfigurationCollection;
import com.google.devtools.build.lib.analysis.config.BuildOptions;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.concurrent.ThreadSafety.ThreadSafe;
import com.google.devtools.build.lib.packages.Package;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A Skyframe value representing a build configuration collection.
 */
@Immutable
@ThreadSafe
public class ConfigurationCollectionValue implements SkyValue {

  private final BuildConfigurationCollection configurationCollection;
  private final ImmutableSet<Package> configurationPackages;

  ConfigurationCollectionValue(BuildConfigurationCollection configurationCollection,
      Set<Package> configurationPackages) {
    this.configurationCollection = Preconditions.checkNotNull(configurationCollection);
    this.configurationPackages = ImmutableSet.copyOf(configurationPackages);
  }

  public BuildConfigurationCollection getConfigurationCollection() {
    return configurationCollection;
  }

  /**
   * Returns set of packages required for configuration.
   */
  public Set<Package> getConfigurationPackages() {
    return configurationPackages;
  }

  @ThreadSafe
  public static SkyKey key(BuildOptions buildOptions, ImmutableSet<String> multiCpu) {
    return new SkyKey(SkyFunctions.CONFIGURATION_COLLECTION, 
        new ConfigurationCollectionKey(buildOptions, multiCpu));
  }

  static final class ConfigurationCollectionKey implements Serializable {
    private final BuildOptions buildOptions;
    private final ImmutableSet<String> multiCpu;
    private final int hashCode;

    public ConfigurationCollectionKey(BuildOptions buildOptions, ImmutableSet<String> multiCpu) {
      this.buildOptions = Preconditions.checkNotNull(buildOptions);
      this.multiCpu = Preconditions.checkNotNull(multiCpu);
      this.hashCode = Objects.hash(buildOptions, multiCpu);
    }

    public BuildOptions getBuildOptions() {
      return buildOptions;
    }

    public ImmutableSet<String> getMultiCpu() {
      return multiCpu;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ConfigurationCollectionKey)) {
        return false;
      }
      ConfigurationCollectionKey confObject = (ConfigurationCollectionKey) o;
      return Objects.equals(multiCpu, confObject.multiCpu)
          && Objects.equals(buildOptions, confObject.buildOptions);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }
  }
}
