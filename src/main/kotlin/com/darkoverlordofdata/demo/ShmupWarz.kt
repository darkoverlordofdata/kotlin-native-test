package com.darkoverlordofdata.demo

import kotlinx.cinterop.*
import platform.posix.*
import sdl.*

inline fun <reified T : CPointed> CPointer<*>?.dereferenceAs(): T = this!!.reinterpret<T>().pointed

data class Mouse(var x:Int, var y:Int, var pressed:Boolean)

class ShmupWarz(var renderer: CPointer<SDL_Renderer>, val width: Int, val height: Int) 
{
    private val pool = Arena()

    var pressed = mutableSetOf<SDL_Keycode>()
    var deactivate = mutableListOf<Int>()
    var running: Boolean = false
    var bullets = mutableListOf<Point2>()
    var enemies1 = mutableListOf<Point2>()
    var enemies2 = mutableListOf<Point2>()
    var enemies3 = mutableListOf<Point2>()
    var explosions = mutableListOf<Point2>()
    var bangs = mutableListOf<Point2>()
    var particles = mutableListOf<Point2>()
    val FONT = "assets/fonts/OpenDyslexic-Bold.otf"
    var delta = 0.0
    val sys: Systems by lazy { Systems(this) }
    val font: CPointer<_TTF_Font> by lazy { TTF_OpenFont(FONT, 28)!! }
    val entities: List<Entity> by lazy { Entities.createLevel(renderer) }

    val isRunning get() = running
    val rect = pool.alloc<SDL_Rect>()
    var mouse = Mouse(0, 0, false)
    val pass: Unit = Unit

    /** message queues */  
    fun removeEntity(id:Int) { deactivate.add(id) }
    fun addBullet(x:Double, y:Double) { listOf(Point2(x, y)) + bullets }
    fun addExplosion(x:Double, y:Double) { listOf(Point2(x, y)) + explosions }
    fun addBang(x:Double, y:Double) { listOf(Point2(x, y)) + bangs }
    fun addParticle(x:Double, y:Double) { listOf(Point2(x, y)) + particles }
    fun addEnemy(enemy:Int) {  
        when(enemy) {
            1 -> listOf(Point2(0.0 , 0.0)) + enemies1
            2 -> listOf(Point2(0.0 , 0.0)) + enemies2
            3 -> listOf(Point2(0.0 , 0.0)) + enemies3
            else -> pass
        }
    }

    fun start() {
        running = true
    }

    fun draw(fsp:Int) {
        
    }

    fun drawEntity(e:Entity) {

    }

    fun drawFps(fps:Int) {

    }

    fun update(delta:Double) {
        SDL_RenderClear(renderer)

        for (e in entities) {
            if (e.active) {
                if (e.category == Category.Background) {
                    SDL_RenderCopy(renderer, e.sprite.texture, null, null)
                } else {
                    rect.w = e.sprite.width
                    rect.h = e.sprite.height
                    rect.x = e.position.x.toInt()
                    rect.y = e.position.y.toInt()
                    SDL_RenderCopy(renderer, e.sprite.texture, null, rect.ptr.reinterpret())
                }
            }
        }
        SDL_RenderPresent(renderer)
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
                    }
                }
            }
        }
    }

}

