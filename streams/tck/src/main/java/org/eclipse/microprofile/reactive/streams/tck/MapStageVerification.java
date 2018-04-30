/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.eclipse.microprofile.reactive.streams.tck;

import org.eclipse.microprofile.reactive.streams.ReactiveStreams;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.testng.Assert.assertEquals;

public class MapStageVerification extends AbstractStageVerification {

  MapStageVerification(ReactiveStreamsTck.VerificationDeps deps) {
    super(deps);
  }

  @Test
  public void mapStageShouldMapElements() {
    assertEquals(await(ReactiveStreams.of(1, 2, 3)
        .map(Object::toString)
        .toList()
        .build(getEngine())), Arrays.asList("1", "2", "3"));
  }

  @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "failed")
  public void mapStageShouldPropagateRuntimeExceptions() {
    await(ReactiveStreams.of("foo")
        .map(foo -> {
          throw new RuntimeException("failed");
        })
        .toList()
        .build(getEngine()));
  }

  @Override
  List<Object> reactiveStreamsTckVerifiers() {
    return Collections.singletonList(
        new ProcessorVerification()
    );
  }

  public class ProcessorVerification extends StageProcessorVerification<Integer> {

    @Override
    public Processor<Integer, Integer> createIdentityProcessor(int bufferSize) {
      return ReactiveStreams.<Integer>builder().map(Function.identity()).build(getEngine());
    }

    @Override
    public Publisher<Integer> createFailedPublisher() {
      return ReactiveStreams.<Integer>failed(new RuntimeException("failed"))
          .map(Function.identity()).build(getEngine());
    }

    @Override
    public Integer createElement(int element) {
      return element;
    }
  }
}
