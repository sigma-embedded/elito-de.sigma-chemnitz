#define _XOPEN_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
	char const		*salt;
	unsigned char		sbuf[sizeof("$1$") + 16] = "$1$";

	if (argc == 3)
		salt = argv[2];
	else {
		int		fd = open("/dev/urandom", O_RDONLY);
		ssize_t		l;
		size_t		i;

		l = read(fd, sbuf+3, sizeof sbuf - 4);
		if (l != sizeof sbuf - 4u) {
			perror("read()");
			return EXIT_FAILURE;
		}

		for (i = 3; i+1 < sizeof sbuf; ++i)
			sbuf[i] = ("0123456789abcdefghijklmnopqrstuvwxyz"
				   "ABCDEFGHIJKLMNOPQRSTUVWXYZ01"[sbuf[i] % 64]);

		sbuf[i] = '\0';
		salt = sbuf;
	}
		
	puts(crypt(argv[1], salt));
	return 0;
}
