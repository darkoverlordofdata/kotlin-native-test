package com.darkoverlordofdata.demo

import kotlinx.cinterop.*
import platform.posix.*
import sdl.*
/**
 * Components
 */

interface IComponent {}
data class Color(var r:Byte, var g:Byte, var b:Byte, var a:Byte) : IComponent
data class Health(var current:Int, var maximum:Int) : IComponent
data class ScaleTween(var min:Double, var max:Double, var speed:Double, var repeat:Boolean, var active:Boolean) : IComponent
data class Sprite(var texture:CPointer<SDL_Texture>?, var width: Int, var height: Int) : IComponent
data class Rectangle(var x:Int, var y:Int, var width:Int, var height:Int) : IComponent
data class Point2(var x:Double, var y:Double) : IComponent
data class Vector2(var x:Double, var y:Double) : IComponent

enum class Actor {
    Default, Background, Text, Lives, Enemy1, Enemy2, Enemy3, 
    Player, Bullet, Explosion, Bang, Particle, Hud
}


enum class Category {
    Background, Bullet, Enemy, Explosion, Particle, Player
}


enum class Effect {
    Pew, Asplode, SmallAsplode
}

enum class Enemies { 
    Enemy1, Enemy2, Enemy3
}

