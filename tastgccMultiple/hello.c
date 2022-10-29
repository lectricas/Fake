#include "hello.h"
#include <string.h>

const char *get_hello() {
    char *world = get_world();
    char *hello = "Hello, ";
    const char *new_allocated[strlen(hello) + strlen(world)];
    strcpy(new_allocated, hello);
    const char *concatinated = strcat(new_allocated, world);
    return concatinated;
}