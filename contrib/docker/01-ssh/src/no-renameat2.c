#include <stddef.h>
#include <stdio.h>
#include <unistd.h>
#include <linux/unistd.h>
#include <linux/seccomp.h>
#include <linux/filter.h>
#include <sys/prctl.h>
#include <errno.h>

#define syscall_nr (offsetof(struct seccomp_data, nr))

struct sock_filter filterout_renameat2[] = {
	BPF_STMT(BPF_LD+BPF_W+BPF_ABS, syscall_nr),
	BPF_JUMP(BPF_JMP+BPF_JEQ+BPF_K, __NR_renameat2, 0, 1),
	BPF_STMT(BPF_RET+BPF_K, SECCOMP_RET_ERRNO + ENOSYS),
	BPF_STMT(BPF_RET+BPF_K, SECCOMP_RET_ALLOW)
};

struct sock_fprog filterout_renameat2_prog = {
	.len = (unsigned short)(sizeof(filterout_renameat2) /
				sizeof(filterout_renameat2[0])),
	.filter = filterout_renameat2,
};

static int disable_renameat2_syscall(void)
{
	int err;
	err = prctl(PR_SET_NO_NEW_PRIVS, 1, 0, 0, 0);
	if (!err) {
		err = prctl(PR_SET_SECCOMP, SECCOMP_MODE_FILTER,
			    &filterout_renameat2_prog);
	}

	return err;
}

int main(int argc, char **argv)
{
	int err = 0;

	err = disable_renameat2_syscall();
	if(err) {
		perror("disable_renameat2_syscall");
	}

	execvp (argv[1], &argv[1]);
	perror("execvp()");
	return 1;
}
