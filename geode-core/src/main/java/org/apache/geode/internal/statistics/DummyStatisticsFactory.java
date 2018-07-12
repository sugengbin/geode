/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.geode.internal.statistics;

import java.io.IOException;
import java.io.Reader;

import org.apache.geode.statistics.StatisticDescriptor;
import org.apache.geode.statistics.Statistics;
import org.apache.geode.statistics.StatisticsFactory;
import org.apache.geode.statistics.StatisticsType;

/**
 * A StatisticsFactory that creates disconnected statistics
 */
public class DummyStatisticsFactory implements StatisticsFactory {

  private final StatisticsFactory factory;

  /** Creates a new instance of DummyStatisticsFactory */
  public DummyStatisticsFactory(StatisticsFactory factory) {
    this.factory = factory;
  }

  // StatisticsFactory methods
  public Statistics createStatistics(StatisticsType type) {
    return createStatistics(type, null, 1);
  }

  public Statistics createStatistics(StatisticsType type, String textId) {
    return createStatistics(type, textId, 1);
  }

  public Statistics createStatistics(StatisticsType type, String textId, long numericId) {
    Statistics result = new LocalStatisticsImpl(type, textId, numericId, 1, false, 0, null);
    return result;
  }

  // /** for internal use only. Its called by {@link LocalStatisticsImpl#close}. */
  // public void destroyStatistics(Statistics stats) {
  // if (statsList.remove(stats)) {
  // statsListModCount++;
  // }
  // }
  //
  public Statistics createAtomicStatistics(StatisticsType type) {
    return createAtomicStatistics(type, null, 1);
  }

  public Statistics createAtomicStatistics(StatisticsType type, String textId) {
    return createAtomicStatistics(type, textId, 1);
  }

  public Statistics createAtomicStatistics(StatisticsType type, String textId, long numericId) {
    Statistics result = new LocalStatisticsImpl(type, textId, numericId, 1, true, 0, null);
    return result;
  }

  public Statistics[] findStatisticsByType(StatisticsType type) {
    return new Statistics[0];
  }

  public Statistics[] findStatisticsByTextId(String textId) {
    return new Statistics[0];
  }

  public Statistics[] findStatisticsByNumericId(long numericId) {
    return new Statistics[0];
  }

  public StatisticsType createType(String name, String description, StatisticDescriptor[] stats) {
    return factory.createType(name, description, stats);
  }

  public StatisticsType findType(String name) {
    return factory.findType(name);
  }

  public StatisticsType[] createTypesFromXml(Reader reader) throws IOException {
    return factory.createTypesFromXml(reader);
  }

  public StatisticDescriptor createIntCounter(String name, String description, String units) {
    return factory.createIntCounter(name, description, units);
  }

  public StatisticDescriptor createLongCounter(String name, String description, String units) {
    return factory.createLongCounter(name, description, units);
  }

  public StatisticDescriptor createDoubleCounter(String name, String description, String units) {
    return factory.createDoubleCounter(name, description, units);
  }

  public StatisticDescriptor createIntGauge(String name, String description, String units) {
    return factory.createIntGauge(name, description, units);
  }

  public StatisticDescriptor createLongGauge(String name, String description, String units) {
    return factory.createLongGauge(name, description, units);
  }

  public StatisticDescriptor createDoubleGauge(String name, String description, String units) {
    return factory.createDoubleGauge(name, description, units);
  }

  public StatisticDescriptor createIntCounter(String name, String description, String units,
      boolean largerBetter) {
    return factory.createIntCounter(name, description, units, largerBetter);
  }

  public StatisticDescriptor createLongCounter(String name, String description, String units,
      boolean largerBetter) {
    return factory.createLongCounter(name, description, units, largerBetter);
  }

  public StatisticDescriptor createDoubleCounter(String name, String description, String units,
      boolean largerBetter) {
    return factory.createDoubleCounter(name, description, units, largerBetter);
  }

  public StatisticDescriptor createIntGauge(String name, String description, String units,
      boolean largerBetter) {
    return factory.createIntGauge(name, description, units, largerBetter);
  }

  public StatisticDescriptor createLongGauge(String name, String description, String units,
      boolean largerBetter) {
    return factory.createLongGauge(name, description, units, largerBetter);
  }

  public StatisticDescriptor createDoubleGauge(String name, String description, String units,
      boolean largerBetter) {
    return factory.createDoubleGauge(name, description, units, largerBetter);
  }

}
