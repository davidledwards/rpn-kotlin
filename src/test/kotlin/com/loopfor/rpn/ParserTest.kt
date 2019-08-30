/*
 * Copyright 2019 David Edwards
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loopfor.rpn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserTest {
    @Test
    fun `valid randomized expressions`() {
        for ((expr, ast) in tests) {
            try {
                val a = Parser.create(Lexer.create(expr))
                assertEquals(a, ast)
            } catch (e: Exception) {
                fail<Unit>(e.message)
            }
        }
    }

    @Test
    fun `invalid expressions`() {
        val tests = listOf(
            "",
            " ",
            "a +",
            "+ a",
            "a 1",
            "(a + 1",
            "a + 1)",
            "(a + 1))",
            ")a",
            "()",
            "a + * 1",
            "a $ 1",
            "(a + 1)(b + 2)"
            )

        for (expr in tests) {
            try {
                Parser.create(Lexer.create(expr))
                fail<Unit>(expr)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Randomly generated by [Tools.parserTests].
     */
    private val tests = listOf<Pair<String, AST>>(
        Pair("Pl max 0.82 ",
            MaxAST(
                SymbolAST("Pl"),
                NumberAST(0.82)
            )),
        Pair("TP ",
            SymbolAST("TP")),
        Pair("0.65 ^ 0.24 ",
            PowerAST(
                NumberAST(0.65),
                NumberAST(0.24)
            )),
        Pair("WR * 0.53 + 0.29 - Vh ",
            SubtractAST(
                AddAST(
                    MultiplyAST(
                        SymbolAST("WR"),
                        NumberAST(0.53)
                    ),
                    NumberAST(0.29)
                ),
                SymbolAST("Vh")
            )),
        Pair("( 0.36 * ( 0.69 ) ) ",
            MultiplyAST(
                NumberAST(0.36),
                NumberAST(0.69)
            )),
        Pair("( ( ( ( AY min ta ) ^ 0.39 % RK min ( 0.77 * ( 0.86 ) ) ) ) max 0.87 ) * 0.84 * ma ",
            MultiplyAST(
                MultiplyAST(
                    MaxAST(
                        ModuloAST(
                            PowerAST(
                                MinAST(
                                    SymbolAST("AY"),
                                    SymbolAST("ta")
                                ),
                                NumberAST(0.39)
                            ),
                            MinAST(
                                SymbolAST("RK"),
                                MultiplyAST(
                                    NumberAST(0.77),
                                    NumberAST(0.86)
                                )
                            )
                        ),
                        NumberAST(0.87)
                    ),
                    NumberAST(0.84)
                ),
                SymbolAST("ma")
            )),
        Pair("QX * Dc / 0.48 - tC ",
            SubtractAST(
                DivideAST(
                    MultiplyAST(
                        SymbolAST("QX"),
                        SymbolAST("Dc")
                    ),
                    NumberAST(0.48)
                ),
                SymbolAST("tC")
            )),
        Pair("ic / 0.75 ",
            DivideAST(
                SymbolAST("ic"),
                NumberAST(0.75)
            )),
        Pair("VU * ( 0.15 ) + fr * AP ",
            AddAST(
                MultiplyAST(
                    SymbolAST("VU"),
                    NumberAST(0.15)
                ),
                MultiplyAST(
                    SymbolAST("fr"),
                    SymbolAST("AP")
                )
            )),
        Pair("0.57 ",
            NumberAST(0.57)),
        Pair("( XV min ( Rn min Dq ) ^ kT % ( 0.76 ^ zn ) ) * 0.36 ",
            MultiplyAST(
                ModuloAST(
                    PowerAST(
                        MinAST(
                            SymbolAST("XV"),
                            MinAST(
                                SymbolAST("Rn"),
                                SymbolAST("Dq")
                            )
                        ),
                        SymbolAST("kT")
                    ),
                    PowerAST(
                        NumberAST(0.76),
                        SymbolAST("zn")
                    )
                ),
                NumberAST(0.36)
            )),
        Pair("Ko max KI ",
            MaxAST(
                SymbolAST("Ko"),
                SymbolAST("KI")
            )),
        Pair("0.55 ",
            NumberAST(0.55)),
        Pair("0.75 % ( 0.25 * 0.10 + 0.75 + ( sj / 0.61 ^ ( 0.42 min IJ - 0.71 min 0.26 ) ) ) ^ Qt max 0.47 ",
            PowerAST(
                ModuloAST(
                    NumberAST(0.75),
                    AddAST(
                        AddAST(
                            MultiplyAST(
                                NumberAST(0.25),
                                NumberAST(0.1)
                            ),
                            NumberAST(0.75)
                        ),
                        PowerAST(
                            DivideAST(
                                SymbolAST("sj"),
                                NumberAST(0.61)
                            ),
                            SubtractAST(
                                MinAST(
                                    NumberAST(0.42),
                                    SymbolAST("IJ")
                                ),
                                MinAST(
                                    NumberAST(0.71),
                                    NumberAST(0.26)
                                )
                            )
                        )
                    )
                ),
                MaxAST(
                    SymbolAST("Qt"),
                    NumberAST(0.47)
                )
            )),
        Pair("OQ * ( ( JN + VD ) max ( 0.73 ) ) ",
            MultiplyAST(
                SymbolAST("OQ"),
                MaxAST(
                    AddAST(
                        SymbolAST("JN"),
                        SymbolAST("VD")
                    ),
                    NumberAST(0.73)
                )
            )),
        Pair("CX ^ pU ",
            PowerAST(
                SymbolAST("CX"),
                SymbolAST("pU")
            )),
        Pair("zZ * ( 0.76 ^ 0.27 - Pu * 0.80 ) min 0.81 ",
            MultiplyAST(
                SymbolAST("zZ"),
                MinAST(
                    SubtractAST(
                        PowerAST(
                            NumberAST(0.76),
                            NumberAST(0.27)
                        ),
                        MultiplyAST(
                            SymbolAST("Pu"),
                            NumberAST(0.8)
                        )
                    ),
                    NumberAST(0.81)
                )
            )),
        Pair("0.50 / ( 0.86 ) + ( Af max 0.50 % Bh min 0.18 ) ",
            AddAST(
                DivideAST(
                    NumberAST(0.5),
                    NumberAST(0.86)
                ),
                ModuloAST(
                    MaxAST(
                        SymbolAST("Af"),
                        NumberAST(0.5)
                    ),
                    MinAST(
                        SymbolAST("Bh"),
                        NumberAST(0.18)
                    )
                )
            )),
        Pair("QN + Vb ^ 0.04 ",
            AddAST(
                SymbolAST("QN"),
                PowerAST(
                    SymbolAST("Vb"),
                    NumberAST(0.04)
                )
            )),
        Pair("( Si - pe ^ 0.51 ) - ( 0.65 ) ",
            SubtractAST(
                SubtractAST(
                    SymbolAST("Si"),
                    PowerAST(
                        SymbolAST("pe"),
                        NumberAST(0.51)
                    )
                ),
                NumberAST(0.65)
            )),
        Pair("( RQ max 0.29 ^ ( ( hx + sx ) * ( 0.14 ^ 0.04 ) ) ) ",
            PowerAST(
                MaxAST(
                    SymbolAST("RQ"),
                    NumberAST(0.29)
                ),
                MultiplyAST(
                    AddAST(
                        SymbolAST("hx"),
                        SymbolAST("sx")
                    ),
                    PowerAST(
                        NumberAST(0.14),
                        NumberAST(0.04)
                    )
                )
            )),
        Pair("0.46 min 0.95 ",
            MinAST(
                NumberAST(0.46),
                NumberAST(0.95)
            )),
        Pair("( ( bu ) * ( nL ) max ( MP + 0.19 ) ) max Vp min ( 0.90 + QH % 0.49 ) min ( 0.66 - 0.09 ^ 0.15 ) ",
            MinAST(
                MinAST(
                    MaxAST(
                        MultiplyAST(
                            SymbolAST("bu"),
                            MaxAST(
                                SymbolAST("nL"),
                                AddAST(
                                    SymbolAST("MP"),
                                    NumberAST(0.19)
                                )
                            )
                        ),
                        SymbolAST("Vp")
                    ),
                    AddAST(
                        NumberAST(0.9),
                        ModuloAST(
                            SymbolAST("QH"),
                            NumberAST(0.49)
                        )
                    )
                ),
                SubtractAST(
                    NumberAST(0.66),
                    PowerAST(
                        NumberAST(0.09),
                        NumberAST(0.15)
                    )
                )
            )),
        Pair("wX ",
            SymbolAST("wX")),
        Pair("Sw max 0.14 max Ly * 0.31 ",
            MultiplyAST(
                MaxAST(
                    MaxAST(
                        SymbolAST("Sw"),
                        NumberAST(0.14)
                    ),
                    SymbolAST("Ly")
                ),
                NumberAST(0.31)
            )),
        Pair("ZQ min Tu - ( EE min 0.39 * wi - ( ZU + ( 0.44 + ( ( ( OK ) - 0.51 - 0.21 ) ) ) - ( YV ) / ( ( rP ) min ( ( ( Pz ) - ( Ku * fF ^ ( yO ) % 0.46 ) + 0.89 - RW ) ) max 0.80 ) ) ) ",
            SubtractAST(
                MinAST(
                    SymbolAST("ZQ"),
                    SymbolAST("Tu")
                ),
                SubtractAST(
                    MultiplyAST(
                        MinAST(
                            SymbolAST("EE"),
                            NumberAST(0.39)
                        ),
                        SymbolAST("wi")
                    ),
                    SubtractAST(
                        AddAST(
                            SymbolAST("ZU"),
                            AddAST(
                                NumberAST(0.44),
                                SubtractAST(
                                    SubtractAST(
                                        SymbolAST("OK"),
                                        NumberAST(0.51)
                                    ),
                                    NumberAST(0.21)
                                )
                            )
                        ),
                        DivideAST(
                            SymbolAST("YV"),
                            MaxAST(
                                MinAST(
                                    SymbolAST("rP"),
                                    SubtractAST(
                                        AddAST(
                                            SubtractAST(
                                                SymbolAST("Pz"),
                                                ModuloAST(
                                                    PowerAST(
                                                        MultiplyAST(
                                                            SymbolAST("Ku"),
                                                            SymbolAST("fF")
                                                        ),
                                                        SymbolAST("yO")
                                                    ),
                                                    NumberAST(0.46)
                                                )
                                            ),
                                            NumberAST(0.89)
                                        ),
                                        SymbolAST("RW")
                                    )
                                ),
                                NumberAST(0.8)
                            )
                        )
                    )
                )
            )),
        Pair("( 0.03 ) % ( 0.12 max ( ob * Yr - 0.46 ) * lY ) ^ ( ( ( ho + oP - ( 0.77 / ( Wi ) ) ) ) ^ WL % 0.05 + 0.84 ) / ( 0.73 ) ",
            DivideAST(
                PowerAST(
                    ModuloAST(
                        NumberAST(0.03),
                        MultiplyAST(
                            MaxAST(
                                NumberAST(0.12),
                                SubtractAST(
                                    MultiplyAST(
                                        SymbolAST("ob"),
                                        SymbolAST("Yr")
                                    ),
                                    NumberAST(0.46)
                                )
                            ),
                            SymbolAST("lY")
                        )
                    ),
                    AddAST(
                        ModuloAST(
                            PowerAST(
                                SubtractAST(
                                    AddAST(
                                        SymbolAST("ho"),
                                        SymbolAST("oP")
                                    ),
                                    DivideAST(
                                        NumberAST(0.77),
                                        SymbolAST("Wi")
                                    )
                                ),
                                SymbolAST("WL")
                            ),
                            NumberAST(0.05)
                        ),
                        NumberAST(0.84)
                    )
                ),
                NumberAST(0.73)
            )),
        Pair("wY ",
            SymbolAST("wY")),
        Pair("( ME ) * yZ / 0.98 ",
            DivideAST(
                MultiplyAST(
                    SymbolAST("ME"),
                    SymbolAST("yZ")
                ),
                NumberAST(0.98)
            )),
        Pair("Ks ",
            SymbolAST("Ks")),
        Pair("0.35 ",
            NumberAST(0.35)),
        Pair("0.85 ",
            NumberAST(0.85)),
        Pair("( uZ / 0.37 * gs ) max ( Kg min 0.31 * ( ( hT max ( Vt min 0.68 ) % pT ) min 0.69 ) ) % ( 0.39 ) max ( iD ^ 0.18 ^ dA ) ",
            ModuloAST(
                MaxAST(
                    MultiplyAST(
                        DivideAST(
                            SymbolAST("uZ"),
                            NumberAST(0.37)
                        ),
                        SymbolAST("gs")
                    ),
                    MultiplyAST(
                        MinAST(
                            SymbolAST("Kg"),
                            NumberAST(0.31)
                        ),
                        MinAST(
                            ModuloAST(
                                MaxAST(
                                    SymbolAST("hT"),
                                    MinAST(
                                        SymbolAST("Vt"),
                                        NumberAST(0.68)
                                    )
                                ),
                                SymbolAST("pT")
                            ),
                            NumberAST(0.69)
                        )
                    )
                ),
                MaxAST(
                    NumberAST(0.39),
                    PowerAST(
                        PowerAST(
                            SymbolAST("iD"),
                            NumberAST(0.18)
                        ),
                        SymbolAST("dA")
                    )
                )
            )),
        Pair("0.11 min YW + cs max si ",
            AddAST(
                MinAST(
                    NumberAST(0.11),
                    SymbolAST("YW")
                ),
                MaxAST(
                    SymbolAST("cs"),
                    SymbolAST("si")
                )
            )),
        Pair("( 0.42 % ( 0.18 % ( 0.01 * rs ) % 0.86 min pf ) ) ",
            ModuloAST(
                NumberAST(0.42),
                ModuloAST(
                    ModuloAST(
                        NumberAST(0.18),
                        MultiplyAST(
                            NumberAST(0.01),
                            SymbolAST("rs")
                        )
                    ),
                    MinAST(
                        NumberAST(0.86),
                        SymbolAST("pf")
                    )
                )
            )),
        Pair("0.48 / 0.45 % vi ",
            ModuloAST(
                DivideAST(
                    NumberAST(0.48),
                    NumberAST(0.45)
                ),
                SymbolAST("vi")
            )),
        Pair("0.20 ",
            NumberAST(0.2)),
        Pair("0.13 max Jg + gk * CQ ",
            AddAST(
                MaxAST(
                    NumberAST(0.13),
                    SymbolAST("Jg")
                ),
                MultiplyAST(
                    SymbolAST("gk"),
                    SymbolAST("CQ")
                )
            )),
        Pair("0.48 max Nj ",
            MaxAST(
                NumberAST(0.48),
                SymbolAST("Nj")
            )),
        Pair("( xu + 0.75 ) ^ ( ( QK + ( ( ( nm ) - kf + 0.70 % qX ) ) ^ eM min Im ) - 0.56 - Qs min 0.89 ) ^ dL ",
            PowerAST(
                PowerAST(
                    AddAST(
                        SymbolAST("xu"),
                        NumberAST(0.75)
                    ),
                    SubtractAST(
                        SubtractAST(
                            AddAST(
                                SymbolAST("QK"),
                                PowerAST(
                                    AddAST(
                                        SubtractAST(
                                            SymbolAST("nm"),
                                            SymbolAST("kf")
                                        ),
                                        ModuloAST(
                                            NumberAST(0.7),
                                            SymbolAST("qX")
                                        )
                                    ),
                                    MinAST(
                                        SymbolAST("eM"),
                                        SymbolAST("Im")
                                    )
                                )
                            ),
                            NumberAST(0.56)
                        ),
                        MinAST(
                            SymbolAST("Qs"),
                            NumberAST(0.89)
                        )
                    )
                ),
                SymbolAST("dL")
            )),
        Pair("( ZD + sL / ( bS - 0.19 ) + Ps ) * ( 0.05 * ( 0.54 ^ ( ( 0.18 ) ) / Pm ) ) ",
            MultiplyAST(
                AddAST(
                    AddAST(
                        SymbolAST("ZD"),
                        DivideAST(
                            SymbolAST("sL"),
                            SubtractAST(
                                SymbolAST("bS"),
                                NumberAST(0.19)
                            )
                        )
                    ),
                    SymbolAST("Ps")
                ),
                MultiplyAST(
                    NumberAST(0.05),
                    DivideAST(
                        PowerAST(
                            NumberAST(0.54),
                            NumberAST(0.18)
                        ),
                        SymbolAST("Pm")
                    )
                )
            )),
        Pair("0.38 + 0.67 + ( 0.30 - ( UZ - ( ( 0.20 / 0.84 + gt max 0.92 ) ^ ( 0.44 / 0.47 ) + 0.69 ) / 0.89 ) % 0.62 * ( Cb max 0.94 ) ) + 0.13 ",
            AddAST(
                AddAST(
                    AddAST(
                        NumberAST(0.38),
                        NumberAST(0.67)
                    ),
                    SubtractAST(
                        NumberAST(0.3),
                        MultiplyAST(
                            ModuloAST(
                                SubtractAST(
                                    SymbolAST("UZ"),
                                    DivideAST(
                                        AddAST(
                                            PowerAST(
                                                AddAST(
                                                    DivideAST(
                                                        NumberAST(0.2),
                                                        NumberAST(0.84)
                                                    ),
                                                    MaxAST(
                                                        SymbolAST("gt"),
                                                        NumberAST(0.92)
                                                    )
                                                ),
                                                DivideAST(
                                                    NumberAST(0.44),
                                                    NumberAST(0.47)
                                                )
                                            ),
                                            NumberAST(0.69)
                                        ),
                                        NumberAST(0.89)
                                    )
                                ),
                                NumberAST(0.62)
                            ),
                            MaxAST(
                                SymbolAST("Cb"),
                                NumberAST(0.94)
                            )
                        )
                    )
                ),
                NumberAST(0.13)
            )),
        Pair("0.94 * ( 0.03 ) % ( 0.58 / ( 0.83 ) * ( 0.35 - rc ) ) ",
            ModuloAST(
                MultiplyAST(
                    NumberAST(0.94),
                    NumberAST(0.03)
                ),
                MultiplyAST(
                    DivideAST(
                        NumberAST(0.58),
                        NumberAST(0.83)
                    ),
                    SubtractAST(
                        NumberAST(0.35),
                        SymbolAST("rc")
                    )
                )
            )),
        Pair("( LT max ( 0.67 ^ ( hE ) ) ) + BM + 0.61 max ( 0.87 % 0.57 ) ",
            AddAST(
                AddAST(
                    MaxAST(
                        SymbolAST("LT"),
                        PowerAST(
                            NumberAST(0.67),
                            SymbolAST("hE")
                        )
                    ),
                    SymbolAST("BM")
                ),
                MaxAST(
                    NumberAST(0.61),
                    ModuloAST(
                        NumberAST(0.87),
                        NumberAST(0.57)
                    )
                )
            )),
        Pair("Uj ",
            SymbolAST("Uj")),
        Pair("( jY - nD min FD + xm ) ",
            AddAST(
                SubtractAST(
                    SymbolAST("jY"),
                    MinAST(
                        SymbolAST("nD"),
                        SymbolAST("FD")
                    )
                ),
                SymbolAST("xm")
            )),
        Pair("0.98 / ( Yb ) ",
            DivideAST(
                NumberAST(0.98),
                SymbolAST("Yb")
            )),
        Pair("( fo - 0.03 - 0.80 ) ^ ( ( 0.51 ) ) ",
            PowerAST(
                SubtractAST(
                    SubtractAST(
                        SymbolAST("fo"),
                        NumberAST(0.03)
                    ),
                    NumberAST(0.8)
                ),
                NumberAST(0.51)
            )),
        Pair("su ",
            SymbolAST("su")),
        Pair("( hY * 0.65 ) ^ ( ( VP ) min ( ( Nb - SR * 0.40 ) * dO + 0.50 ) % nM ^ 0.49 ) ",
            PowerAST(
                MultiplyAST(
                    SymbolAST("hY"),
                    NumberAST(0.65)
                ),
                PowerAST(
                    ModuloAST(
                        MinAST(
                            SymbolAST("VP"),
                            AddAST(
                                MultiplyAST(
                                    SubtractAST(
                                        SymbolAST("Nb"),
                                        MultiplyAST(
                                            SymbolAST("SR"),
                                            NumberAST(0.4)
                                        )
                                    ),
                                    SymbolAST("dO")
                                ),
                                NumberAST(0.5)
                            )
                        ),
                        SymbolAST("nM")
                    ),
                    NumberAST(0.49)
                )
            )))
}
