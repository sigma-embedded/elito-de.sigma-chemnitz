#define _XOPEN_SOURCE

#include <stdio.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
	puts(crypt(argv[1], argv[2]));
	return 0;
}
