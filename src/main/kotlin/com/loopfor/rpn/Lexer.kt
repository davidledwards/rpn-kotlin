package com.loopfor.rpn

interface Lexer : (Sequence<Char>) -> Sequence<Token> {
    companion object {
        fun create(): Lexer = BasicLexer()
        fun create(ins: Sequence<Char>): Sequence<Token> = create()(ins)
        fun create(ins: String): Sequence<Token> = create()(ins.asSequence())
    }
}

private class BasicLexer : Lexer {
    override fun invoke(ins: Sequence<Char>): Sequence<Token> {
        tailrec fun tokens(ins: Sequence<Char>): Sequence<Token> {
            val (token, _ins) = tokenize(ins)
            return if (token == EOSToken)
                emptySequence<Token>()
            else
                sequenceOf<Token>(token) + tokens(_ins)
        }
        return tokens(ins)
    }

    private tailrec fun tokenize(ins: Sequence<Char>): Pair<Token, Sequence<Char>> {
        val c = ins.firstOrNull()
        return when (c) {
            in WHITESPACE ->
                tokenize(ins.drop(1))
            in SIMPLE_TOKENS ->
                SIMPLE_TOKENS.get(c)!! to ins.drop(1)
            in DIGITS ->
                readNumber(ins.drop(1), "$c")
            in LETTERS ->
                readSymbol(ins.drop(1), "$c")
            null ->
                EOSToken to ins
            else ->
                throw Exception("$c: unrecognized character")
        }
    }

    private tailrec fun readNumber(ins: Sequence<Char>, lexeme: String): Pair<Token, Sequence<Char>> {
        val c = ins.firstOrNull()
        return when (c) {
            '.' ->
                if (c in lexeme)
                    throw Exception("$lexeme: malformed number")
                else
                    readNumber(ins.drop(1), lexeme + '.')
            in DIGITS ->
                readNumber(ins.drop(1), lexeme + c)
            else ->
                if (lexeme.last() == '.')
                    throw Exception("$lexeme: malformed number")
                else
                    NumberToken(lexeme) to ins
        }
    }

    private tailrec fun readSymbol(ins: Sequence<Char>, lexeme: String): Pair<Token, Sequence<Char>> {
        val c = ins.firstOrNull()
        return if (c in LETTERS)
            readSymbol(ins.drop(1), lexeme + c)
        else
            (SYMBOL_TOKENS.get(lexeme) ?: SymbolToken(lexeme)) to ins
    }

    private val DIGITS = ('0'..'9').toSet()
    private val LETTERS = (('A'..'Z') + ('a'..'z')).toSet()
    private val WHITESPACE = setOf(' ', '\n', '\r', '\t', '\u000c')
}
