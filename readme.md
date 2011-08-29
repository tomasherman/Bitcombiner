# Bitcombiner
Simple clone of scala parser combinator library designed for bit data

##Examples

### Check if the stream begins with either [1 1] [1 0] 

	import all the needed stuff from package net.tomasherman.bitcombiner

	scala> import net.tomasherman.bitrex.Utils._
	import net.tomasherman.bitcombiner.Utils._

	scala> import net.tomasherman.bitrex.Parsers._
	import net.tomasherman.bitcombiner.Parsers._

	scala> val data = bitStream(Array(0xaa,0xaa))
	data: Stream[net.tomasherman.bitcombiner.Bit] = Stream(1, ?)

	scala> import net.tomasherman.bitcombiner._
	import net.tomasherman.bitcombiner._

	scala> def twoOnes = litSeq(b1,b1)
	twoOnes: java.lang.Object with net.tomasherman.bitcombiner.BitParser[IndexedSeq[net.tomasherman.bitcombiner.Bit]]

	scala> def oneZero = litSeq(b1,b0)
	oneZero: java.lang.Object with net.tomasherman.bitcombiner.BitParser[IndexedSeq[net.tomasherman.bitcombiner.Bit]]

	scala> def twoOnesOrOneZero = twoOnes | oneZero
	twoOnesOrOneZero: net.tomasherman.bitcombiner.DisjunctParser[Any]

	scala> twoOnesOrOneZero(data)
	res0: net.tomasherman.bitcombiner.Result[Any] = Success(Vector(1, 0),Stream(1, ?))

##Thanks to
 - [Daniel Spiewak for amazing post](http://www.codecommit.com/blog/scala/the-magic-behind-parser-combinators) which i used and abused to make this work <3