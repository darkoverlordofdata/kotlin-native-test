package com.darkoverlordofdata.demo

import kotlinx.cinterop.*
import platform.posix.*
import sdl.*

/**
 * this class demonstrates how messy kotlin syntax is
 * when compared to scala...
 * it even breaks the syntax highlighter!
 */
class Systems(val game: ShmupWarz) 
{
    var enemyT1: Double = 2.0
    var enemyT2: Double = 7.0
    var enemyT3: Double = 13.0
    var FireRate: Double = 0.1
    var timeToFire: Double = 0.0
    val Tau = 6.28318

    /** 
     * Handle player input
     */
    fun input(entity:Entity, delta:Double):Entity {
        if (!entity.active) return entity

        return when (entity.category) {
            Category.Player -> {
                val x = game.mouse.x.toDouble()
                val y = game.mouse.y.toDouble()
                if (game.pressed.contains( SDLK_z.toInt() ) || game.mouse.pressed) { // z
                    timeToFire -= delta
                    if (timeToFire < 0.0) {
                        game.addBullet(entity.position.x - 27, entity.position.y + 2) 
                        game.addBullet(entity.position.x + 27, entity.position.y + 2)
                        timeToFire = FireRate
                    }
                }
                entity.copy(position=Point2(x, y))
            }
            else -> entity
        }
    }

    /**
     * Motion
     */
    fun physics(entity:Entity, delta:Double):Entity {
        if (!entity.active) return entity
        if (entity.velocity == null) return entity

        val x = entity.position.x + entity.velocity.x * delta
        val y = entity.position.y + entity.velocity.y * delta
        val x1 = (x-entity.bounds.width/2).toInt()
        val y1 = (y-entity.bounds.height/2).toInt()

        return entity.copy(
            position = Point2(x, y), 
            bounds = Rectangle(x1, y1, entity.bounds.width, entity.bounds.height)
        )
    }
    
    /**
     * Expire enities
     */
    fun expire(entity:Entity, delta:Double):Entity {
        if (!entity.active) return entity
        if (entity.expires == null) return entity

        val exp = entity.expires - delta
        return entity.copy(
            expires = exp, 
            active = (exp > 0.0)
        )

    }

    /**
     * Tween 
     */
    fun tween(entity:Entity, delta:Double):Entity {
        if (!entity.active) return entity
        if (entity.scaleTween == null) return entity

        var x = entity.scale.x + (entity.scaleTween.speed * delta)
        var y = entity.scale.y + (entity.scaleTween.speed * delta)
        var active = entity.scaleTween.active

        if (x > entity.scaleTween.max) {
            x = entity.scaleTween.max
            y = entity.scaleTween.max
            active = false
        } else if (x < entity.scaleTween.min) {
            x = entity.scaleTween.min
            y = entity.scaleTween.min
            active = false
        }
        return entity.copy(
            scale = Vector2(x, y), 
            scaleTween = ScaleTween(entity.scaleTween.min, entity.scaleTween.max, entity.scaleTween.speed, entity.scaleTween.repeat, active)
        )

    }

    /**
     * remove offscreen entities
     */
    fun remove(entity:Entity, delta:Double):Entity {
        if (!entity.active) return entity
        return when (entity.category) {
            Category.Enemy -> {
                if (entity.position.y > game.height) 
                    entity.copy(active = false)
                else entity
            }
            Category.Bullet -> {
                if (entity.position.y < 0) 
                    entity.copy(active = false)
                else entity
            }
            else -> entity
        }
    }

    /**
     * Spawn enemies
     */
    fun spawn(delta:Double) {
        fun spawnEnemy(t:Double, enemy:Int):Double {
            val d1 = t-delta
            if (d1 < 0.0) {
                game.addEnemy(enemy)
                return when (enemy) {
                    1 -> 2.0
                    2 -> 7.0
                    3 -> 13.0
                    else -> 0.0
                }
            } else return d1
        }
        enemyT1 = spawnEnemy(enemyT1, 1)
        enemyT2 = spawnEnemy(enemyT2, 2)
        enemyT3 = spawnEnemy(enemyT3, 3)
    }

    /**
     * create entities from que
     */
    fun create(entity:Entity, delta:Double):Entity {
        if (entity.active == true) {
            val ix = game.deactivate.indexOf(entity.id)
            if (ix != -1) {
                game.deactivate.removeAt(ix)
                return entity.copy(active = false)
            } else return entity
        }
        return when (entity.actor) {    
            Actor.Bullet -> {
                if (game.bullets.isEmpty())
                    entity
                else  {
                    val bullet = game.bullets.first()
                    game.bullets = game.bullets.drop(1)
                    Entities.bullet(entity, bullet.x, bullet.y)
                }
            }
            Actor.Enemy1 -> {
                if (game.enemies1.isEmpty())
                    entity
                else  {
                    val enemy1 = game.enemies1.first()
                    game.enemies1 = game.enemies1.drop(1)
                    Entities.enemy1(entity, game.width)
                }
            }
            Actor.Enemy2 -> {
                if (game.enemies2.isEmpty())
                    entity
                else  {
                    val enemy2 = game.enemies2.first()
                    game.enemies2 = game.enemies2.drop(1)
                    Entities.enemy2(entity, game.width)
                }
            }
            Actor.Enemy3 -> {
                if (game.enemies3.isEmpty())
                    entity
                else  {
                    val enemy3 = game.enemies3.first()
                    game.enemies3 = game.enemies3.drop(1)
                    Entities.enemy3(entity, game.width)
                }
            }
            Actor.Explosion -> {
                if (game.explosions.isEmpty())
                    entity
                else  {
                    val explosion = game.explosions.first()
                    game.explosions = game.explosions.drop(1)
                    Entities.explosion(entity, explosion.x, explosion.y)
                }
            }   
            Actor.Bang -> {
                if (game.bangs.isEmpty())
                    entity
                else  {
                    val bang = game.bangs.first()
                    game.bangs = game.bangs.drop(1)
                    Entities.bang(entity, bang.x, bang.y)
                }
            }   
            Actor.Particle -> {
                if (game.particles.isEmpty())
                    entity
                else  {
                    val particle = game.particles.first()
                    game.particles = game.particles.drop(1)
                    Entities.particle(entity, particle.x, particle.y)
                }
            }     
            else -> entity;
        }
    }

    /**
     * Handle collisions
     */
    fun collision(entity:Entity, delta:Double):Entity {

        fun handleCollision(a: Entity, b: Entity):Entity {
            game.addBang(b.position.x, b.position.y)
            game.removeEntity(b.id)
            for (i in 0..3) {
                game.addParticle(b.position.x, b.position.y)
            }
            if (a.health == null) 
                return a
            
            val h = a.health.current -2
            if (h < 0) {
                game.addExplosion(b.position.x, b.position.y)
                return a.copy(active=false)
            }
            return a.copy(health=Health(h, a.health.maximum))
        }

        fun intersects(a:Entity, b:Entity):Boolean {
            val r1 = a.bounds
            val r2 = b.bounds
            return ((r1.x < r2.x + r2.width) && 
                    (r1.x + r1.width > r2.x) && 
                    (r1.y < r2.y + r2.height) && 
                    (r1.y + r1.height > r2.y)) 
        }

        fun collide(entity:Entity):Entity  {
            for (bullet in game.entities) 
                if (bullet.active && bullet.category == Category.Bullet) 
                    if (intersects(entity, bullet))
                        return handleCollision(entity, bullet) 
                    else 
                        return entity

           return  entity
        }

        if (entity.active && entity.category == Category.Enemy) 
            return collide(entity)

        return entity
    }


}