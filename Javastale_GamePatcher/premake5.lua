solution "javastale_gamepatcher"
   language "C++"
   location "project"
   targetdir "build/release"

   flags { "Optimize", "NoMinimalRebuild", "NoFramePointer", "FloatFast", "NoBufferSecurityCheck", "NoIncrementalLink", "NoManifest", "NoRTTI", "NoExceptions", "StaticRuntime"}

   if os.is("linux") or os.is("macosx") then
      buildoptions {"-m32 -fPIC -ldl -lstdc++"}
      linkoptions  {"-m32 -fPIC -ldl -lstdc++"}
   else
	linkoptions {"/SAFESEH:NO"}
   end
   

   vpaths {
      ["Header Files/*"] = { "src/**.h","src/**.hpp" },
      ["Source Files/*"] = { "src/**.c","src/**.cpp" },
   }

   kind "SharedLib"

   configurations { "Debug", "Release" }

   files { "src/**.h", "src/**.cpp" }
   
   includedirs { }
   
   links { }
   
   -- A project defines one build target
   project "javastale_gamepatcher"
      targetname "javastale_gamepatcher"

      configuration "Release"
         defines { "NDEBUG", "_GENERIC" }
      configuration "Debug"
         defines { "DEBUG", "_GENERIC" }
         flags { "Symbols", "EnableSSE2" }
         targetdir "build/debug"