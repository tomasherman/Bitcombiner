package net.tomasherman.bitcombiner

sealed trait Bit
object b0 extends Bit { override def toString = "0" }
object b1 extends Bit { override def toString = "1" }

object Utils {

	implicit def i2b(x:Int) = x.asInstanceOf[Byte]

	def intPow(x:Int,p:Int):Int = {
		p match {
			case 0 => 1
			case _ => x*intPow(x,p-1)
		}
	}

	def bitAt(b:Byte,pos:Int) = {
		b & intPow(2,pos) match {
			case 0 => b0
			case _ => b1
		}
	}


	def bitStreamRec(a:Array[Byte],byte:Int,pos:Int):Stream[Bit] = {
		if(byte == a.length){
			Stream.empty
		} else {
			val nextByte = {if(pos == 7) {byte + 1} else {byte}}
			val cBit = bitAt(a(byte),7-pos)
			val nextBit = (pos+1) % 8
			Stream.cons(cBit,bitStreamRec(a,nextByte,nextBit)) 
		}
	}

	def bitStream(a:Array[Byte]):Stream[Bit] = {
		bitStreamRec(a,0,0)
	}
}
sealed trait Result[+A]
case class Success[+A](value: A, rem: Stream[Bit]) extends Result[A]
case class Failure(reason:String) extends Result[Nothing]

trait BitParser[+A] extends (Stream[Bit] => Result[A]){
	def ~[B](righty: =>BitParser[B]) = new SeqParser(this,righty)
	def |[B](righty: =>BitParser[B]) = new DisjunctParser(this,righty)
}

object Parsers {
	def litSeq(xs: Bit*) = new BitParser[IndexedSeq[Bit]] {
		def apply(s:Stream[Bit]) = {
			val bits = s take xs.length
			val (_,outcome) = ((0,true) /: bits) ( (state,x) => (state._1+1, state._2 && (x == xs(state._1))))
			if(outcome) Success(bits.toIndexedSeq[Bit],s drop xs.length)
			else Failure("%s expected but %s found!" format(xs,bits))
		}
	}
}

class SeqParser[+A,+B](lefty: => BitParser[A],righty: => BitParser[B]) extends BitParser[(A,B)]{
	lazy val l = lefty
	lazy val r = righty

	def apply(s:Stream[Bit]) = {
		l(s) match {
			case Success(a,ss) => r(ss) match {
				case Success(b,sss) => Success((a,b),sss)
				case x:Failure => x 
			}
			case x:Failure => x
		}
	}
}

class DisjunctParser[A](lefty: => BitParser[A],righty: => BitParser[A]) extends BitParser[A]{
	lazy val l = lefty
	lazy val r = righty

	def apply(s:Stream[Bit]) = {
		l(s) match {
			case x:Success[_] => x
			case _ => r(s)
		}
	}
}

