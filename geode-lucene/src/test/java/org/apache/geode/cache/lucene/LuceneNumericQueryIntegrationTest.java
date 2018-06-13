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

package org.apache.geode.cache.lucene;

import static org.apache.geode.distributed.ConfigurationProperties.SERIALIZABLE_OBJECT_FILTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.junit.categories.DistributedTest;
import org.apache.geode.test.junit.rules.ServerStarterRule;

@Category(DistributedTest.class)
public class LuceneNumericQueryIntegrationTest {

  @Rule
  public ServerStarterRule serverStarterRule = new ServerStarterRule()
      .withPDXPersistent()
      .withPDXReadSerialized()
      .withProperty(SERIALIZABLE_OBJECT_FILTER, "org.apache.geode.cache.lucene.*")
      .withAutoStart();

  private InternalCache cache;



  private static final SomeDomain someDomain1 =
      SomeDomain.newBuilder()
          .withStrField("strField1")
          .withIntField(110)
          .withLongField(10010L)
          .withFloatField(101.25F)
          .withDoubleField(101.25D)
          .withDateField(
              Date.from(LocalDateTime.parse("2001-01-01T00:00:00").toInstant(ZoneOffset.UTC)))
          .build();

  private static final SomeDomain someDomain2 =
      SomeDomain.newBuilder()
          .withStrField("strField2")
          .withIntField(120)
          .withLongField(10020L)
          .withFloatField(201.25F)
          .withDoubleField(201.25D)
          .withDateField(
              Date.from(LocalDateTime.parse("2002-01-01T00:00:00").toInstant(ZoneOffset.UTC)))
          .build();

  private static final SomeDomain someDomain3 =
      SomeDomain.newBuilder()
          .withStrField("strField3")
          .withIntField(130)
          .withLongField(10030L)
          .withFloatField(301.25F)
          .withDoubleField(301.25D)
          .withDateField(
              Date.from(LocalDateTime.parse("2003-01-01T00:00:00").toInstant(ZoneOffset.UTC)))
          .build();


  @Before
  public void beforeTest() {
    this.cache = serverStarterRule.getCache();
    LuceneService luceneService = LuceneServiceProvider.get(this.cache);
    luceneService.createIndexFactory()
        .setFields("strField", "intField", "dateField", "longField", "floatField", "doubleField")
        .create("idx1",
            "/sampleregion");

    this.cache.<String, SomeDomain>createRegionFactory(RegionShortcut.PARTITION)
        .create("sampleregion");

    Region<String, SomeDomain> region = this.cache.getRegion("/sampleregion");

    region.put(someDomain1.getStrField(), someDomain1);
    region.put(someDomain2.getStrField(), someDomain2);
    region.put(someDomain3.getStrField(), someDomain3);
  }


  @Test
  public void testByIntegerRange() throws Exception {

    QueryService queryService = this.cache.getQueryService();

    Query query = queryService.newQuery("select * from /sampleregion");
    SelectResults results = (SelectResults) query.execute();
    assertThat(results).hasSize(3);

    LuceneService luceneService = LuceneServiceProvider.get(cache);

    LuceneQuery<String, SomeDomain> luceneQuery1 = luceneService.createLuceneQueryFactory()
        .create("idx1", "/sampleregion", "+strField=strField* +intField:[110 TO 120]",
            "strField");

    assertThat(luceneQuery1.findKeys())
        .containsExactlyInAnyOrder("strField1", "strField2");

    assertThat(luceneQuery1.<SomeDomain>findValues())
        .containsExactlyInAnyOrder(someDomain1, someDomain2);


    LuceneQuery<String, SomeDomain> luceneQuery2 = luceneService.createLuceneQueryFactory()
        .create("idx1", "/sampleregion", "+strField=strField* +intField:[120 TO 130]",
            "strField");

    assertThat(luceneQuery2.findKeys())
        .containsExactlyInAnyOrder("strField2", "strField3");

    assertThat(luceneQuery2.<SomeDomain>findValues())
        .containsExactlyInAnyOrder(someDomain2, someDomain3);

  }


}
