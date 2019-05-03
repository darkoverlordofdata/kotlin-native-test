package com.darkoverlordofdata.demo

import kotlinx.cinterop.*
import platform.posix.*
import sdl.*

inline fun <reified T : CPointed> CPointer<*>?.dereferenceAs(): T = this!!.reinterpret<T>().pointed
inline fun get_SDL_Error() = SDL_GetError()!!.toKString()

data class Mouse(var x:Int, var y:Int, var pressed:Boolean)

class Game(val width: Int, val height: Int) {

    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private val window: CPointer<SDL_Window>
    private val renderer: CPointer<SDL_Renderer>
    private val platform: String
    private val pool = Arena()
    private var running: Boolean = false
    var entities: List<Entity>
    val player: Entity
    val count: Int
    val isRunning get() = running
        
    val rect = pool.alloc<SDL_Rect>()
    var mouse = Mouse(0, 0, false)

    init {
        // srand(time(null).toInt())
        srand(time(null).toUInt())
        
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

        platform = SDL_GetPlatform()!!.toKString()

        memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            if (SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()) != 0) {
                println("SDL_GetCurrentDisplayMode Error: ${get_SDL_Error()}")
                SDL_Quit()
                throw Error()
            }
            displayWidth = displayMode.w
            displayHeight = displayMode.h
        }
        
        val window = SDL_CreateWindow("Shmupwarz [${platform}]", 100, 100, width, height, SDL_WINDOW_SHOWN)
        if (window == null) {
            println("SDL_CreateWindow Error: ${get_SDL_Error()}")
            SDL_Quit()
            throw Error()
        }
        this.window = window

        val renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC)
        if (renderer == null) {
            SDL_DestroyWindow(window)
            println("SDL_CreateRenderer Error: ${get_SDL_Error()}")
            SDL_Quit()
            throw Error()
        }
        this.renderer = renderer
        entities = Entities.createLevel(renderer)
        count = entities.size
        player = entities[count-1]
    }

    fun update() {
        SDL_RenderClear(renderer)
        // val rect = pool.alloc<SDL_Rect>()

        for (e in entities) {
            if (e.active) {
                rect.w = e.sprite.width
                rect.h = e.sprite.height
                rect.x = e.position.x.toInt()
                rect.y = e.position.y.toInt()
                SDL_RenderCopy(renderer, e.sprite.texture, null, rect.ptr.reinterpret())
            }
        }
        SDL_RenderPresent(renderer)
    }

    fun start() {
        running = true
    }

    /**
    * Handle Events
    */
    fun handleEvents() {
        memScoped {
            val event = alloc<SDL_Event>()
            while (SDL_PollEvent(event.ptr.reinterpret()) != 0) {
                when (event.type) {
                    SDL_QUIT -> {
                        running = false
                    }
                    SDL_KEYDOWN -> {
                        val keyboardEvent = event.ptr.reinterpret<SDL_KeyboardEvent>().pointed
                        when (keyboardEvent.keysym.scancode) {
                            SDL_SCANCODE_ESCAPE -> running = false
                        }
                    }
                    SDL_MOUSEBUTTONUP -> {
                        mouse.pressed = false
                    }
                    SDL_MOUSEBUTTONDOWN -> {
                        val mouseEvent = event.ptr.reinterpret<SDL_MouseButtonEvent>().pointed
                        mouse.x = mouseEvent.x
                        mouse.y = mouseEvent.y
                        mouse.pressed = true
                    }
                    SDL_MOUSEMOTION -> {
                        val mouseEvent = event.ptr.reinterpret<SDL_MouseMotionEvent>().pointed
                        mouse.x = mouseEvent.x
                        mouse.y = mouseEvent.y
                        player.position.x = mouse.x.toDouble() - 108/2
                        player.position.y = mouse.y.toDouble() - 172/2
                    }
                }
            }
        }
    }


    fun destroy() {
        SDL_DestroyTexture(entities[0].sprite.texture)
        SDL_DestroyRenderer(renderer)
        SDL_DestroyWindow(window)
        SDL_Quit()
    }
}

