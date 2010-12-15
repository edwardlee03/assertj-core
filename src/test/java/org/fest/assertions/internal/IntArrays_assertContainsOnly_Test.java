/*
 * Created on Dec 14, 2010
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright @2010 the original author or authors.
 */
package org.fest.assertions.internal;

import static org.fest.assertions.error.DoesNotContainOnly.doesNotContainOnly;
import static org.fest.assertions.test.Arrays.arrayOfInts;
import static org.fest.assertions.test.ExpectedException.none;
import static org.fest.assertions.test.FailureMessages.*;
import static org.fest.assertions.util.ArrayWrapperList.wrap;
import static org.fest.util.Collections.set;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.fest.assertions.core.*;
import org.fest.assertions.test.ExpectedException;
import org.junit.*;

/**
 * Tests for <code>{@link IntArrays#assertContainsOnly(AssertionInfo, int[], int[])}</code>.
 *
 * @author Alex Ruiz
 */
public class IntArrays_assertContainsOnly_Test {

  private static WritableAssertionInfo info;

  @Rule public ExpectedException thrown = none();

  private Failures failures;
  private int[] actual;
  private IntArrays arrays;

  @BeforeClass public static void setUpOnce() {
    info = new WritableAssertionInfo();
  }

  @Before public void setUp() {
    failures = spy(Failures.instance());
    actual = arrayOfInts(6, 8, 10);
    arrays = new IntArrays(failures);
  }

  @Test public void should_pass_if_actual_contains_given_values_only() {
    arrays.assertContainsOnly(info, actual, arrayOfInts(6, 8, 10));
  }

  @Test public void should_pass_if_actual_contains_given_values_only_in_different_order() {
    arrays.assertContainsOnly(info, actual, arrayOfInts(10, 8, 6));
  }

  @Test public void should_pass_if_actual_contains_given_values_only_more_than_once() {
    actual = arrayOfInts(6, 8, 10, 8, 8, 8);
    arrays.assertContainsOnly(info, actual, arrayOfInts(6, 8, 10));
  }

  @Test public void should_pass_if_actual_contains_given_values_only_even_if_duplicated() {
    arrays.assertContainsOnly(info, actual, arrayOfInts(6, 8, 10, 6, 8, 10));
  }

  @Test public void should_throw_error_if_array_of_values_to_look_for_is_empty() {
    thrown.expectIllegalArgumentException(arrayToLookForIsEmpty());
    arrays.assertContainsOnly(info, actual, new int[0]);
  }

  @Test public void should_throw_error_if_array_of_values_to_look_for_is_null() {
    thrown.expectNullPointerException(arrayToLookForIsNull());
    arrays.assertContainsOnly(info, actual, null);
  }

  @Test public void should_fail_if_actual_is_null() {
    thrown.expectAssertionError(unexpectedNull());
    arrays.assertContainsOnly(info, null, arrayOfInts(8));
  }

  @Test public void should_fail_if_actual_does_not_contain_given_values_only() {
    int[] expected = { 6, 8, 20 };
    try {
      arrays.assertContainsOnly(info, actual, expected);
      fail();
    } catch (AssertionError e) {}
    verify(failures).failure(info, doesNotContainOnly(wrap(actual), wrap(expected), set(10), set(20)));
  }
}
