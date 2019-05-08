/*
 * MIT License
 * 
 * Copyright (c) 2017 Bruce Davidson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/
import kotlinx.cinterop.*
import platform.posix.*
import sdl.*

inline fun get_SDL_Error() = SDL_GetError()!!.toKString()

fun main()
{
    srand(time(null).toUInt())
    
    val fileName = "shmupwarz.log"
    val file = fopen(fileName, "wt")
    if (file == null) throw Error("Cannot write file '$fileName'")
    // try {
    //     fputs("Hello! Привет!", file)
    // } finally {
    //     fclose(file)
    // }    
    if (SDL_Init(SDL_INIT_EVERYTHING) != 0) {
        println("SDL_Init Error: ${get_SDL_Error()}")
        throw Error()
    }

    // if (IMG_Init(IMG_INIT_PNG) != IMG_INIT_PNG) {
    if (IMG_Init(IMG_INIT_PNG.toInt()) != IMG_INIT_PNG.toInt()) {
        println("Unable to init image")
        throw Error()
    }
    if (TTF_Init() == -1) {
        println("Unable to init truetype fonts")
        throw Error()
    }
    // if (Mix_OpenAudio(MIX_DEFAULT_FREQUENCY, MIX_DEFAULT_FORMAT.toShort(), 2, 2048) == -1) {
    if (Mix_OpenAudio(MIX_DEFAULT_FREQUENCY, MIX_DEFAULT_FORMAT, 2, 2048) == -1) {
        println("Unable to init mixer")
        throw Error()
    }

    val title  = "ShmupWarz"
    val width  = 640
    val height = 512
    val u = 1000000000.0
    var mark1 = kotlin.system.getTimeNanos().toDouble() / u
    var mark2 = 0.0
    var delta = 0.0
    var d = 0.0
    var fps = 60
    var k = 0
    var t = 0.0
    var k2 = 0


    val window = SDL_CreateWindow(title, SDL_WINDOWPOS_CENTERED.toInt(), SDL_WINDOWPOS_CENTERED.toInt(), width, height, SDL_WINDOW_SHOWN)
    if (window == null) {
            println("SDL_CreateWindow Error: ${get_SDL_Error()}")
            SDL_Quit()
            throw Error()
    }
    val renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_PRESENTVSYNC)
    if (renderer == null) {
        SDL_DestroyWindow(window)
        println("SDL_CreateRenderer Error: ${get_SDL_Error()}")
        SDL_Quit()
        throw Error()
    }
    val game = com.darkoverlordofdata.demo.ShmupWarz(renderer!!, width, height)

    game.start()

    try {
        fputs("Start game!\n", file)

        while (game.running) {
            mark2 = kotlin.system.getTimeNanos().toDouble() / u
            delta = mark2 - mark1
            mark1 = mark2
            k += 1
            d += delta
            if (d >= 1.0) {
                fps = k
                k = 0
                d = 0.0
            }
            game.handleEvents()
            val m1 = kotlin.system.getTimeNanos().toDouble() / u
            game.update(delta)
            val m2 = kotlin.system.getTimeNanos().toDouble() / u
            k2 = k2 +1
            t = t + (m2 - m1)
            if (k2 >= 1000) {
                // fputs("${t/1000.0}\n", file)
                // fputs(String.format("%.5f", t/1000.0), file)
                fputs("${t}\n", file)
                k2 = 0
                t = 0.0
            }
            game.draw(fps)
        }
    } finally {
        fclose(file)
    }    

    SDL_DestroyRenderer(renderer)
    SDL_DestroyWindow(window)
    SDL_Quit()

}

