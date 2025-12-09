package dev.slne.surf.shulker.runtime.common.exception

class DuplicatePlatformActionException(val name: String) : RuntimeException(
    "A platform action with the name '$name' already exists."
)