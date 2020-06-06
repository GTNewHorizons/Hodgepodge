package com.github.bartimaeusnek.modmixins.core

import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.ModClassLoader
import net.minecraft.launchwrapper.LaunchClassLoader
import java.io.File
import java.net.URL

object ClassPreLoader {
    private val modClassLoaderField = Loader::class.java.getDeclaredField("modClassLoader")
    private val loaderinstanceField  = Loader::class.java.getDeclaredField("instance")
    private var loader : Any
    private var modClassLoader : ModClassLoader
    private val mainClassLoaderField = ModClassLoader::class.java.getDeclaredField("mainClassLoader")
    private var mainClassLoader : LaunchClassLoader

    init {
        loaderinstanceField.isAccessible = true
        modClassLoaderField.isAccessible = true
        mainClassLoaderField.isAccessible = true
        loader = loaderinstanceField.get(null)
        modClassLoader = modClassLoaderField.get(loader) as ModClassLoader
        mainClassLoader = mainClassLoaderField.get(modClassLoader) as LaunchClassLoader
    }

    @Throws(Exception::class)
    fun loadJar(pathToJar: File) {
        modClassLoader.addFile(pathToJar)
    }

    @Throws(Exception::class)
    fun unloadJar(pathToJar: File) {
        val url: URL = pathToJar.toURI().toURL()
        mainClassLoader.sources.removeIf { urlb -> url == urlb }
    }
}