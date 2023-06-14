package com.oakware.parsercombinator

data class MutableString(var stringWrapped: String) {
    fun advance(steps: Int = 1) {
        stringWrapped = stringWrapped.drop(steps)
    }
}

fun interface Parser<Out> {
    operator fun invoke(input: MutableString): Out?
}

/**
 * Extension to support String parameter
 */
operator fun <T> Parser<T>.invoke(input: String) = invoke(MutableString(input))

/**
 * Char parser
 */
val charParser = Parser { input ->
    input.stringWrapped.firstOrNull()
        ?.also { input.advance() }
}

val intParser = Parser { input ->
    input.stringWrapped.firstOrNull()?.digitToIntOrNull()
        ?.also { input.advance() }
}

val letterParser = Parser { input ->
    input.stringWrapped.firstOrNull()
        ?.takeIf { it.isLetter() }
        ?.also { input.advance() }
}

fun literal(char: Char) = Parser { input ->
    input.stringWrapped.firstOrNull()
        ?.takeIf { it == char }
        ?.also { input.advance() }
}

/**
 * Zip two parsers into a pair
 */
fun <A, B> zip(
    a: Parser<A>,
    b: Parser<B>
): Parser<Pair<A, B>> = Parser {
    val originalState = it.stringWrapped

    val resultA = a(it)
    val resultB = b(it)

    if (resultA != null && resultB != null) {
        Pair(resultA, resultB)
    } else {
        // restore to the original state
        it.stringWrapped = originalState
        null
    }
}

fun <A, B, C> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>
): Parser<Triple<A, B, C>> = Parser {
    val originalState = it.stringWrapped

    val zippedAB = zip(a, b)
    val result1 = zippedAB(it)
    val resultC = c(it)
    if (result1 != null && resultC != null) {
        Triple(result1.first, result1.second, resultC)
    } else {
        it.stringWrapped = originalState
        null
    }
}

fun <A, B> Parser<A>.map(
    transform: (A) -> B,
): Parser<B> = Parser {
    invoke(it)?.let(transform)
}

/*
fun <A, B, C> zip(
    a: Parser<A>,
    b: Parser<B>,
    c: Parser<C>,
): Parser<Triple<A, B, C>> = zip(zip(a, b), c)
    .map { (a, c): Pair<Pair<A, B>, C> -> Triple(a.first, a.second, c) }
*/


fun <Out> zeroOrMore(parser: Parser<Out>) = Parser<List<Out>> {
    val results = mutableListOf<Out>()
    while (true) {
        val result = parser(it) ?: break
        results.add(result)
    }
    results
}

fun <Out> oneOrMore(parser: Parser<Out>) = Parser<List<Out>> {
    val results = mutableListOf<Out>()

    while(true) {
        val result = parser(it) ?: break
        results.add(result)
    }

    // return null if empty
    if (results.isEmpty()) null else results
    // results.takeIf { it.isNotEmpty() }
}


val wordValueParser = oneOrMore(letterParser).map { it: List<Char> ->
    it.joinToString("")
}

val numericValueParser = oneOrMore(intParser).map { it: List<Int> ->
    it.joinToString("").toInt()
}

val separatorParser = literal(':')


data class KeyValue(val key: String, val value: Int)

/**
 * Parser for pattern:
 *  `[letterKey]:[numericValue]`
 *
 *  e.g:
 *   - height:100
 *   - size:200
 */
val keyValueParser = zip(
    wordValueParser,
    separatorParser,
    numericValueParser
).map { (key, separator, value) ->
    KeyValue(key, value)
}

fun <A> optional(a: Parser<A>): Parser<Unit> = Parser {
    a(it)
    Unit
}

val linebreak = optional(literal('\n'))

val keyValueRowParser =
    zip(keyValueParser, linebreak)
        .map { (keyValue, _) -> keyValue }

val multiLineParser =
    zeroOrMore(keyValueRowParser)

