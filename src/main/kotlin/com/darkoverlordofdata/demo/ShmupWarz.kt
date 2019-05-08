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
    var bullets = listOf<Point2>()
    var enemies1 = listOf<Point2>()
    var enemies2 = listOf<Point2>()
    var enemies3 = listOf<Point2>()
    var explosions = listOf<Point2>()
    var bangs = listOf<Point2>()
    var particles:List<Point2> = listOf<Point2>()
    val FONT = "assets/fonts/OpenDyslexic-Bold.otf"
    var delta = 0.0
    val sys: Systems by lazy { Systems(this) }
    val font: CPointer<_TTF_Font> by lazy { TTF_OpenFont(FONT, 28)!! }

    lateinit var entities: List<Entity>
    init { entities = Entities.createLevel(renderer) }

    val isRunning get() = running
    val rect = pool.alloc<SDL_Rect>()
    var mouse = Mouse(0, 0, false)
    val pass: Unit = Unit

    /** message queues */  
    fun removeEntity(id:Int)                { deactivate.add(id) }
    fun addBullet(x:Double, y:Double)       { bullets =     listOf(Point2(x, y)) + bullets }
    fun addExplosion(x:Double, y:Double)    { explosions =  listOf(Point2(x, y)) + explosions }
    fun addBang(x:Double, y:Double)         { bangs =       listOf(Point2(x, y)) + bangs }
    fun addParticle(x:Double, y:Double)     { particles =   listOf(Point2(x, y)) + particles }
    fun addEnemy(enemy:Int) {  
        when(enemy) {
            1 -> enemies1 = listOf(Point2(0.0, 0.0)) + enemies1
            2 -> enemies2 = listOf(Point2(0.0, 0.0)) + enemies2
            3 -> enemies3 = listOf(Point2(0.0, 0.0)) + enemies3
            else -> pass
        }
    }

    fun start() {
        running = true
    }

    fun draw(fps:Int) {
        SDL_RenderClear(renderer)
        SDL_SetRenderDrawColor(renderer, 0.toUByte(), 0.toUByte(), 0.toUByte(), 255.toUByte())
        entities.filter{ it.active }.map{ drawEntity(it) }
        //   drawFps(fps)
        SDL_RenderPresent(renderer)
        
    }

    fun drawEntity(e:Entity) {

        if (e.tint != null)
            SDL_SetTextureColorMod(e.sprite.texture, e.tint.r, e.tint.g, e.tint.b)

        if (e.category == Category.Background) {
            SDL_RenderCopy(renderer, e.sprite.texture, null, null) 
        } else {
            rect.w = (e.sprite.width * e.scale.x).toInt()
            rect.h = (e.sprite.height * e.scale.y).toInt()
            rect.x = (e.position.x - rect.w / 2).toInt()
            rect.y = (e.position.y - rect.h / 2).toInt()
            SDL_RenderCopy(renderer, e.sprite.texture, null, rect.ptr.reinterpret()) 
        }
    }

    fun drawFps(fps:Int) {

    }

    fun update(delta:Double) {
        sys.spawn(delta)
        entities = entities
            .map{ sys.collision(it, delta) }
            .map{ sys.create(it, delta) }
            .map{ sys.input(it, delta) }
            .map{ sys.physics(it, delta) }
            .map{ sys.expire(it, delta) }
            .map{ sys.tween(it, delta) }
            .map{ sys.remove(it, delta) }

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
                        pressed.add(keyboardEvent.keysym.sym)
                    }
                    SDL_KEYUP -> {
                        val keyboardEvent = event.ptr.reinterpret<SDL_KeyboardEvent>().pointed
                        pressed.remove(keyboardEvent.keysym.sym)
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
                    else -> pass
                }
            }
        }
    }
}

