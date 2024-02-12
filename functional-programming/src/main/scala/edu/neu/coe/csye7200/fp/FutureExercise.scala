package edu.neu.coe.csye7200.fp

import scala.concurrent._
import scala.language.postfixOps

/**
  * This case class is basically a Future-logger such that when the function has finished executing,
  * we print a comment on the error stream.
  *
  * There are two methods:
  * * the apply method which converts Seq[X] to Future[X] and
  * * the sequence method which converts Seq[Future[X] into Future[X]
  *
  * @param f    the function to apply to apply's input parameter
  * @param name the name of the function
  * @tparam X the underlying type
  */
case class Async[X](f: Seq[X] => X, name: String)(implicit executor: ExecutionContext) extends (Seq[X] => Future[X]) {
  def apply(xs: Seq[X]): Future[X] = Future {
    val x = f(xs)
    System.err.println(s"sequence starting ${xs.head} has $name $x")
    x
  }

  def sequence(xfs: Seq[Future[X]]): Future[X] = for (xs <- Future.sequence(xfs)) yield {
    val x = f(xs)
    System.err.println(s"sequence of futures has $name $x")
    x
  }
}

/**
  * Created by scalaprof on 2/17/17.
  */
object FutureExercise extends App {
  def integers(i: Int, n: Int): LazyList[BigInt] = LazyList.from(i).map(BigInt(_)) take n

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  //e接受一个Seq[x]参数
  val e = Async[BigInt](xs => xs.sum, "sum")
  private val chunk = 10000 // Try it first with chunk = 10000 and build up to 1000000

  //从0到9生成 i * chunk + 1  开始的chunk个数字， 组成一个seq， 传入e中
  // xfs = e([[10001, 10002,...], [20001, 20002, ...].....[90001, 90002, ....]])
  // 对每一个seq， 进行了相加操作
  // xfs是一个Future[BigInt]的Seq
  // xfs = seq[Future[sum1], Future[sum2].....]
  private val xfs = for (i <- 0 to 9) yield e(integers(i * chunk + 1, chunk))
  private val xf = e.sequence(xfs)
  xf foreach { x => println(s"Sum: $x") }
  private val c10 = chunk * 10
  private val expected = xf filter (_ == BigInt((1L + c10) * c10 / 2))
  expected onComplete {
    case scala.util.Success(value) => println(value)
    case scala.util.Failure(x) => System.err.println(x.getLocalizedMessage)
  }
  Await.ready(expected, 10000 milli)
  println("Goodbye")
}

/*
* Async类
Async[X]是一个泛型case类，用于封装一个将Seq[X]转换为X的函数f，并异步执行它。X是底层类型参数。该类接受一个隐式的ExecutionContext，它是执行异步操作所需的执行上下文。

def apply(xs: Seq[X]): Future[X]方法接受一个Seq[X]作为输入，使用Future异步应用函数f，并在操作完成时打印一条消息。这个方法将Seq[X]转换成一个Future[X]。

def sequence(xfs: Seq[Future[X]]): Future[X]方法接受一个Seq[Future[X]]（即一系列异步操作的序列），将它们转换为一个单一的Future[X]。它首先使用Future.sequence来等待所有Future[X]完成，然后对结果序列应用函数f，并在操作完成时打印一条消息。

FutureExercise对象
def integers(i: Int, n: Int): LazyList[BigInt]函数生成一个从i开始的n个整数的LazyList[BigInt]。这个懒加载列表在被实际访问前不会计算其元素值。

在FutureExercise的主体中，首先创建了一个Async[BigInt]实例e，用于异步计算整数序列的和。

使用for循环生成了10个异步任务，每个任务计算一个由chunk定义大小的整数序列的和。这些任务通过调用e(integers(i * chunk + 1, chunk))生成，其中每个序列的起始点由i * chunk + 1确定。

val xf = e.sequence(xfs)将这10个Future[BigInt]合并成一个单一的Future[BigInt]，它包含了所有序列和的总和。

xf foreach { x => println(s"Sum: $x") }在最终和计算完成后打印结果。

val expected = xf filter (_ == BigInt((1L + c10) * c10 / 2))过滤出符合特定条件的结果，即验证最终和是否等于预期值，预期值根据等差数列求和公式计算。

使用Await.ready(expected, 10000 milli)等待expected的完成，最大等待时间为10000毫秒。

expected onComplete {...}处理expected的完成，无论是成功还是失败，都会打印相应的信息。

整个示例展示了如何结合使用Future、函数式编程和LazyList来实现并行计算，并通过Async类辅助记录异步操作的执行状态。这种方式可以有效地利用多核处理器并行执行多个任务，同时通过异步操作避免阻塞主线程。
* */