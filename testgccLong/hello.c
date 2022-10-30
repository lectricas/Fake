#include "hello.h"
#include <string.h>
#include <stdlib.h>

const char *get_hello() {
    char *world = get_world();
    char *hello = "Hello, ";
    char *target = malloc(sizeof(hello) + sizeof(world));
    strcpy(target, hello);
    strcat(target, world);
    return target;
}