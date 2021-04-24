/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2021 the original author or authors.
 */
package org.assertj.core.api.recursive.comparison;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.util.Lists.list;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.RecursiveComparisonAssert_isEqualTo_BaseTest;
import org.assertj.core.internal.objects.data.Human;
import org.assertj.core.internal.objects.data.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RecursiveComparisonAssert_isEqualTo_comparingOnlyFields_Test extends RecursiveComparisonAssert_isEqualTo_BaseTest {

  @ParameterizedTest(name = "{2}: actual={0} / expected={1}")
  @MethodSource
  void should_only_compare_given_fields(Object actual, Object expected, List<String> fieldNamesToCompare) {

    then(actual).usingRecursiveComparison()
                .comparingOnlyFields(arrayOf(fieldNamesToCompare))
                .isEqualTo(expected);
  }

  private static Stream<Arguments> should_only_compare_given_fields() {
    Person person1 = new Person("John");
    person1.home.address.number = 1;

    Person person2 = new Person("John");
    person2.home.address.number = 2;

    Person john = new Person("John");
    john.home.address.number = 1;
    john.dateOfBirth = new Date(123);
    john.neighbour = new Person("Jim");
    john.neighbour.home.address.number = 123;
    john.neighbour.neighbour = new Person("James");
    john.neighbour.neighbour.home.address.number = 124;

    Person jack = new Person("Jack");
    jack.home.address.number = 1;
    jack.dateOfBirth = new Date(456);
    jack.neighbour = new Person("Jack");
    jack.neighbour.home.address.number = 456;
    jack.neighbour.neighbour = new Person("James");
    jack.neighbour.neighbour.home.address.number = 124;

    Human person4 = new Human();
    person4.name = "John";
    person4.home.address.number = 1;

    Human person5 = new Human();
    person5.home.address.number = 1;

    return Stream.of(arguments(person1, person2, list("name")),
                     arguments(person1, person4, list("name")),
                     arguments(person1, person5, list("home")),
                     arguments(person1, person5, list("home.address")),
                     arguments(person1, person5, list("home.address.number")),
                     arguments(john, jack, list("home", "neighbour.neighbour")),
                     arguments(john, jack, list("home.address", "neighbour.neighbour")),
                     arguments(john, jack, list("home.address.number", "neighbour.neighbour")),
                     arguments(john, jack, list("home", "neighbour.neighbour.home")),
                     arguments(john, jack, list("home.address", "neighbour.neighbour")));
  }

  @Test
  void should_fail_when_actual_differs_from_expected_on_compared_fields() {
    // GIVEN
    Person actual = new Person("John");
    actual.home.address.number = 1;
    actual.dateOfBirth = new Date(123);
    actual.neighbour = new Person("Jim");
    actual.neighbour.home.address.number = 123;
    actual.neighbour.neighbour = new Person("James");
    actual.neighbour.neighbour.home.address.number = 124;

    Person expected = new Person("John");
    expected.home.address.number = 1;
    expected.dateOfBirth = new Date(456);
    expected.neighbour = new Person("Jack");
    expected.neighbour.home.address.number = 123;
    expected.neighbour.neighbour = new Person("James");
    expected.neighbour.neighbour.home.address.number = 125;

    recursiveComparisonConfiguration.compareOnlyFields("name", "home", "dateOfBirth", "neighbour");

    // WHEN
    compareRecursivelyFailsAsExpected(actual, expected);

    // THEN
    ComparisonDifference dateOfBirthDifference = diff("dateOfBirth", actual.dateOfBirth, expected.dateOfBirth);
    ComparisonDifference neighbourNameDifference = diff("neighbour.name", actual.neighbour.name, expected.neighbour.name);
    ComparisonDifference numberDifference = diff("neighbour.neighbour.home.address.number",
                                                 actual.neighbour.neighbour.home.address.number,
                                                 expected.neighbour.neighbour.home.address.number);
    verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected,
                                                              dateOfBirthDifference, neighbourNameDifference, numberDifference);
  }

  @Test
  void can_be_combined_with_ignoringFields() {
    // GIVEN
    Person actual = new Person("John");
    actual.home.address.number = 1;
    actual.dateOfBirth = new Date(123);
    actual.neighbour = new Person("Jim");
    actual.neighbour.home.address.number = 123;
    actual.neighbour.neighbour = new Person("James");
    actual.neighbour.neighbour.home.address.number = 124;

    Person expected = new Person(actual.name);
    expected.home.address.number = 2;
    expected.dateOfBirth = new Date(456);
    expected.neighbour = new Person("Jack");
    expected.neighbour.home.address.number = actual.neighbour.home.address.number;
    expected.neighbour.neighbour = new Person(actual.neighbour.neighbour.name);
    expected.neighbour.neighbour.home.address.number = 125;

    // WHEN/THEN
    then(actual).usingRecursiveComparison()
                .comparingOnlyFields("name", "home", "neighbour")
                // ignores all different fields from the compared fields
                .ignoringFields("home.address.number", "neighbour.name", "neighbour.neighbour.home.address.number")
                .isEqualTo(expected);
    then(actual).usingRecursiveComparison()
                // ignores all different fields from the compared fields
                .ignoringFields("home.address.number", "neighbour.name", "neighbour.neighbour.home.address.number")
                .comparingOnlyFields("name", "home", "neighbour")
                .isEqualTo(expected);
  }

  private static String[] arrayOf(List<String> list) {
    return list.toArray(new String[0]);
  }

}