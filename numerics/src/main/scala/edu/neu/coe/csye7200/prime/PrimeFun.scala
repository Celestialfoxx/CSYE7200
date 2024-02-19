package edu.neu.coe.csye7200.prime

import edu.neu.coe.csye7200.prime.Prime.primes

object PrimeFun extends App {

  // NOTE: This exercise concerns the values of p^2 % n where p is a prime number and n is a "magic number."
  // You can get a lazy list of primes from edu.neu.coe.csye7200.prime.Prime.primes

  // TODO read a set of numbers from the command line (set these with menu item: Run/Edit Configuration ... Program arguments).
  // For each number (called the magicNumber) write out the number and the first 100 values, skipping the first two.
  // The numbers should start after 12 and you shouldn't need more than 12 to see the pattern.
  // The pattern should be obvious in just the first 10 results.
  // Submit the file (Question 1)

  // TODO using the one magic number that gives you the pattern, get a list of the first 100,000 numbers (again excluding the first two).
  // Try to find the first number that doesn't match the pattern (there may be none).

  private val numbers: Array[Int] = args map (_.toInt)

  private def show(magicNumber: Int): String = {
    // SOLUTION
    // STUB

    val ys = primes.drop(2).take(100).map(p => (p.x.pow(2) % magicNumber).toString)
    // END

    s"""$magicNumber: ${ys.mkString(",")}"""
  }

  numbers.foreach(n => println(show(n)))

  for (n <- numbers) println(show(n))

  val magicPatternNumber = 101

  val largePatternTest = primes.drop(2).take(100000).map(p => (p.x.pow(2) % magicPatternNumber)).toList

  val firstDeviationIndex = largePatternTest.zipWithIndex.sliding(2).find {
    case Seq((prev, _), (current, idx)) => prev != current}.map(_(1)._2)

  firstDeviationIndex match {
    case Some(index) => println(s"The first number doesn't match is $index，value is ${largePatternTest(index)}")
    case None => println("No such number")
  }

}
