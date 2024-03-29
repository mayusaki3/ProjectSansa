
set(gem_path ${CMAKE_CURRENT_LIST_DIR})
set(gem_json ${gem_path}/gem.json)
o3de_restricted_path(${gem_json} gem_restricted_path gem_parent_relative_path)

# Currently we are in the pong/Code folder: ${CMAKE_CURRENT_LIST_DIR}
# Get the platform specific folder ${pal_dir} for the current folder: ${CMAKE_CURRENT_LIST_DIR}/Platform/${PAL_PLATFORM_NAME}
# Note: o3de_pal_dir will take care of the details for us, as this may be a restricted platform
#       in which case it will see if that platform is present here or in the restricted folder.
#       i.e. It could here : pong/Code/Platform/<platform_name>  or
#            <restricted_folder>/<platform_name>/pong/Code
o3de_pal_dir(pal_dir ${CMAKE_CURRENT_LIST_DIR}/Platform/${PAL_PLATFORM_NAME} ${gem_restricted_path} ${gem_path} ${gem_parent_relative_path})

# Now that we have the platform abstraction layer (PAL) folder for this folder, thats where we will find the
# traits for this platform. Traits for a platform are defines for things like whether or not something in this project
# is supported by this platform.
include(${pal_dir}/PAL_${PAL_PLATFORM_NAME_LOWERCASE}.cmake)

# Now that we have loaded our project traits for this platform, see if this project is even supported on this platform.
# If its not supported we just return after including the unsupported.
if(NOT PAL_TRAIT_PONG_SUPPORTED)
    return()
endif()

# We are on a supported platform, so add the pong target
# Note: We include the common files and the platform specific files which are set in pong_files.cmake and
# in ${pal_dir}/pong_${PAL_PLATFORM_NAME_LOWERCASE}_files.cmake
ly_add_target(
    NAME pong.Static STATIC
    NAMESPACE Gem
    FILES_CMAKE
        pong_files.cmake
        ${pal_dir}/pong_${PAL_PLATFORM_NAME_LOWERCASE}_files.cmake
    INCLUDE_DIRECTORIES
        PUBLIC
            Include
    BUILD_DEPENDENCIES
        PRIVATE
            AZ::AzGameFramework
            Gem::Atom_AtomBridge.Static
)

ly_add_target(
    NAME pong ${PAL_TRAIT_MONOLITHIC_DRIVEN_MODULE_TYPE}
    NAMESPACE Gem
    FILES_CMAKE
        pong_shared_files.cmake
        ${pal_dir}/pong_shared_${PAL_PLATFORM_NAME_LOWERCASE}_files.cmake
    INCLUDE_DIRECTORIES
        PUBLIC
            Include
    BUILD_DEPENDENCIES
        PRIVATE
            Gem::pong.Static
            AZ::AzCore
)

# if enabled, pong is used by all kinds of applications
ly_create_alias(NAME pong.Builders NAMESPACE Gem TARGETS Gem::pong)
ly_create_alias(NAME pong.Tools    NAMESPACE Gem TARGETS Gem::pong)
ly_create_alias(NAME pong.Clients  NAMESPACE Gem TARGETS Gem::pong)
ly_create_alias(NAME pong.Servers  NAMESPACE Gem TARGETS Gem::pong)

################################################################################
# Gem dependencies
################################################################################

# Enable the specified list of gems from GEM_FILE or GEMS list for this specific project:
ly_enable_gems(PROJECT_NAME pong GEM_FILE enabled_gems.cmake)
