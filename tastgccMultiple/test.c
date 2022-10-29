#include "hello.h"
#include <assert.h>
#include <stdio.h>
#include <string.h>

int main() {
    printf("Starting\n");
    assert(strcmp(get_hello(), "Hello, World!") == 0);
    printf("All good\n");
    return 0;
}