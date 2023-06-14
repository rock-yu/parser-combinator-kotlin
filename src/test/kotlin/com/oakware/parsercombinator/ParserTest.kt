package com.oakware.parsercombinator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ParserTest {
    @Test
    fun charParserValid() {
        val input = MutableString("abc")
        val out: Char? = charParser(input)
        assertEquals('a', out)
        assertEquals("bc", input.stringWrapped)
    }

    @Test
    fun intParserValid() {
        val input = MutableString("123")
        val out: Int? = intParser(input)
        assertEquals(1, out)
        assertEquals("23", input.stringWrapped)
    }

    @Test
    fun intParserInvalid() {
        val input = MutableString("abc")
        val out: Int? = intParser(input)
        assertNull(out)
        assertEquals("abc", input.stringWrapped)
    }

    @Test
    fun letterParser() {
        val input = MutableString("a123")
        val output: Char? = letterParser(input)
        assertEquals('a', output)
        assertEquals("123", input.stringWrapped)
    }

    @Test
    fun letterParserInvalid() {
        val input = MutableString("123")
        val output: Char? = letterParser(input)
        assertNull(output)
        assertEquals("123", input.stringWrapped)
    }

    @Test
    fun zipPair() {
        val zippedParser: Parser<Pair<Int, Char>> = zip(intParser, charParser)

        val input = MutableString("3XYZ")
        val output: Pair<Int, Char>? = zippedParser(input)
        assertEquals(Pair(3, 'X'), output)
        assertEquals("YZ", input.stringWrapped)
    }

    @Test
    fun zipTriple() {
        val zippedParser: Parser<Triple<Int, Char, Int>> = zip(intParser, charParser, intParser)
        val input = MutableString("3X2")
        val output: Triple<Int, Char, Int>? = zippedParser(input)
        assertNotNull(output)
        assertEquals(Triple(3, 'X', 2), output)
    }

    @Test
    fun zeroOrMore() {
        val zeroOrMore = zeroOrMore(intParser)

        val input = MutableString("123X")
        val output: List<Int>? = zeroOrMore(input)

        assertNotNull(output)
        assertEquals(listOf(1, 2, 3), output)
    }

    @Test
    fun zeroOrMoreInvalid() {
        val zeroOrMore = zeroOrMore(intParser)

        val input = MutableString("X")
        val output: List<Int>? = zeroOrMore(input)

        assertNotNull(output)
        assertEquals(emptyList(), output)
    }

    @Test
    fun oneOrMore() {
        val oneOrMore = oneOrMore(intParser)

        val input = MutableString("123X")
        val output: List<Int>? = oneOrMore(input)

        assertNotNull(output)
        assertEquals(listOf(1, 2, 3), output)
    }

    @Test
    fun oneOrMoreInvalid() {
        val oneOrMore = oneOrMore(intParser)

        val input = MutableString("XYZ")
        val output: List<Int>? = oneOrMore(input)

        assertNull(output)
    }

    @Test
    fun separatorParser() {
        val input = MutableString(":XY")
        val output = separatorParser(input)
        assertEquals(':', output)
        assertEquals("XY", input.stringWrapped)
    }

    @Test
    fun keyValueParser() {
        val input = MutableString("height:100")
        val output: KeyValue? = keyValueParser(input)
        assertEquals(KeyValue("height", 100), output)
    }


    @Test
    fun linebreak() {
        val input = MutableString("\nXY")
        val output = linebreak(input)
        assertEquals('\n', output)
        assertEquals("XY", input.stringWrapped)
    }

    @Test
    fun keyValueRowParser() {
        val input = MutableString("height:100\nwidth:50")
        val output = keyValueRowParser(input)
        assertEquals(KeyValue("height", 100), output)
        assertEquals("width:50", input.stringWrapped)
    }

    @Test
    fun multiLineParser() {
        val configStr = """
            height:100
            width:50
            depth:12
            
            """.trimIndent()

        val input = MutableString(configStr)
        val output: List<KeyValue>? = multiLineParser(input)

        assertEquals(listOf(
            KeyValue("height", 100),
            KeyValue("width", 50),
            KeyValue("depth", 12),
        ), output)
        assertEquals("", input.stringWrapped)

    }
}