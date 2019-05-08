package com.darkoverlordofdata.demo

import kotlin.random.*
import kotlinx.cinterop.*
import platform.posix.*
import sdl.*

/**
 * Entity database record
 */
data class Entity (                    
    var id: Int,                        /* Unique sequential id */
    val name: String,                   /* Display name */
    val active: Boolean,                /* In use */
    val actor: Actor,                   /* Actor Id */
    val category: Category,             /* Category */
    val position: Point2,               /* Position on screen */
    val bounds: Rectangle,              /* Collision bounds */
    val scale: Vector2,                 /* Display scale */
    val sprite: Sprite,                 /* Sprite */
    val sound: Effect?,                 /* Sound effect */
    val tint: Color?,                   /* Color to use as tint */
    val expires: Double?,               /* Countdown until expiration */
    val health: Health?,                /* Track health */
    val scaleTween: ScaleTween?,        /* scale Tweening variables */
    val velocity: Vector2?              /* Cartesian velocity */
)

/**
 * Entity factory
 */
var uniqueId = 0
object Entities {
    fun getUniqueId():Int {
        uniqueId += 1
        return uniqueId
    }

    fun createSprite(renderer:CPointer<SDL_Renderer>, path:String):Sprite  {
        val surface = IMG_Load(path)
        val sprite = Sprite(SDL_CreateTextureFromSurface(renderer, surface), 
                            surface.dereferenceAs<SDL_Surface>().w, 
                            surface.dereferenceAs<SDL_Surface>().h)

        SDL_SetTextureBlendMode(sprite.texture, SDL_BLENDMODE_BLEND)
        return sprite
    }

    fun createBackground(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/background.png")

        return Entity(
            id = getUniqueId(),
            name = "Background",
            active = true,
            actor = Actor.Background,
            category = Category.Background,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    fun createPlayer(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/fighter.png")

        return Entity(
            id = getUniqueId(),
            name = "Player",
            active = true,
            actor = Actor.Player,
            category = Category.Player,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    fun createBullet(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/bullet.png")

        return Entity(
            id = getUniqueId(),
            name = "Bullet",
            active = false,
            actor = Actor.Bullet,
            category = Category.Bullet,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, (sprite.width*2).toInt(), sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = 2.0,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }
    fun createEnemy1(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/enemy1.png")

        return Entity(
            id = getUniqueId(),
            name = "Enemy1",
            active = false,
            actor = Actor.Enemy1,
            category = Category.Enemy,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    fun createEnemy2(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/enemy2.png")

        return Entity(
            id = getUniqueId(),
            name = "Enemy2",
            active = false,
            actor = Actor.Enemy2,
            category = Category.Enemy,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    fun createEnemy3(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/enemy3.png")

        return Entity(
            id = getUniqueId(),
            name = "Enemy3",
            active = false,
            actor = Actor.Enemy3,
            category = Category.Enemy,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }


    fun createExplosion(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/explosion.png")

        return Entity(
            id = getUniqueId(),
            name = "Explosion",
            active = false,
            actor = Actor.Explosion,
            category = Category.Explosion,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    fun createBang(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/explosion.png")

        return Entity(
            id = getUniqueId(),
            name = "Explosion",
            active = false,
            actor = Actor.Bang,
            category = Category.Explosion,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    fun createParticle(renderer:CPointer<SDL_Renderer>):Entity {
        val sprite = createSprite(renderer, "assets/images/star.png")

        return Entity(
            id = getUniqueId(),
            name = "Particle",
            active = false,
            actor = Actor.Particle,
            category = Category.Explosion,
            position = Point2(0.0, 0.0),
            bounds = Rectangle(0, 0, sprite.width, sprite.height),
            sprite = sprite,
            sound = null,
            scale = Vector2(1.0, 1.0),
            tint = null,
            expires = null,
            health = null,
            scaleTween = null,
            velocity = null
        )
    }

    /**
     * Recycle entities from the pool
     */
    fun bullet(e:Entity, x:Double, y:Double):Entity {
        return e.copy(
            active = true,
            position = Point2(x, y),
            expires = 1.0, 
            sound = Effect.Pew,
            health = Health(2, 2),
            tint = Color(0xd2.toUByte(), 0xfa.toUByte(), 0x00.toUByte(), 0xffa.toUByte()),
            velocity = Vector2(0.0, -800.0)
        )
    }
    
    
    fun enemy1(e:Entity, width:Int):Entity {
        return e.copy(
            active = true,
            position = Point2(Random.nextInt(width-35).toDouble(), 92.0/2.0),
            velocity = Vector2(0.0, 40.0),
            health = Health(10, 10)
        )
    }

    fun enemy2(e:Entity, width:Int):Entity {
        return e.copy(
            active = true,
            position = Point2(Random.nextInt(width-85).toDouble(), 172.0/2.0),
            velocity = Vector2(0.0, 30.0),
            health = Health(20, 20)
        )
    }

    fun enemy3(e:Entity, width:Int):Entity {
        return e.copy(active = true,
            position = Point2(Random.nextInt(width-160).toDouble(), 320.0/2.0),
            velocity = Vector2(0.0, 20.0),
            health = Health(60, 60)
        )
    }
    fun explosion(e:Entity, x:Double, y:Double):Entity {
        return e.copy(
            active = true,
            position = Point2(x, y),
            scale = Vector2(0.5, 0.5),
            sound = Effect.Asplode,
            scaleTween = ScaleTween(0.5/100, 0.5, -3.0, false, true),
            tint = Color(0xd2.toUByte(), 0xfa.toUByte(), 0xd2.toUByte(), 0xfa.toUByte()),
            expires = 0.2
        )
    }

    fun bang(e:Entity, x:Double, y:Double):Entity {
        return e.copy(
            active = true,
            position = Point2(x, y),
            scale = Vector2(0.2, 0.2),
            sound = Effect.SmallAsplode,
            scaleTween = ScaleTween(0.2/100, 0.2, -3.0, false, true),
            tint = Color(0xd2.toUByte(), 0xfa.toUByte(), 0xd2.toUByte(), 0xfa.toUByte()),
            expires = 0.2
        )
    }

    fun particle(e:Entity, x:Double, y:Double):Entity {
        val Tau = 6.28318
        val radians = (Random.nextDouble()/1.0) * Tau
        val magnitude = Random.nextInt(100) + 50
        val velocityX = magnitude.toDouble() * Math.cos(radians)
        val velocityY = magnitude.toDouble() * Math.sin(radians)
        val scale = (Random.nextInt(10).toDouble() / 10.0)
        return e.copy(
            active = true,
            position = Point2(x, y),
            scale = Vector2(scale, scale),
            velocity = Vector2(velocityX, velocityY),
            tint = Color(0xfa.toUByte(), 0xfa.toUByte(), 0xd2.toUByte(), 0xff.toUByte()),
            expires = 0.5
        )
    }

  /**
   * Create the level database pool
   */
  fun createLevel(renderer:CPointer<SDL_Renderer>):List<Entity> {
    return arrayOf(
        createBackground(renderer),
        createEnemy1(renderer),
        createEnemy1(renderer),
        createEnemy1(renderer),
        createEnemy1(renderer),
        createEnemy2(renderer),
        createEnemy2(renderer),
        createEnemy2(renderer),
        createEnemy2(renderer),
        createEnemy3(renderer),
        createEnemy3(renderer),
        createEnemy3(renderer),
        createEnemy3(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createParticle(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBullet(renderer),
        createBang(renderer),
        createBang(renderer),
        createBang(renderer),
        createBang(renderer),
        createBang(renderer),
        createBang(renderer),
        createExplosion(renderer),
        createExplosion(renderer),
        createExplosion(renderer),
        createExplosion(renderer),
        createExplosion(renderer),
        createExplosion(renderer),
        createPlayer(renderer)
    ).toList<Entity>()
  }
    
}