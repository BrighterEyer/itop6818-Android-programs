#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/watchdog.h>
int main(void)
{
int fd = open("/dev/watchdog", O_WRONLY);
int timeout = 10;
int count = 0;

if (fd == -1) 
{
        perror("watchdog");
        exit(EXIT_FAILURE);
}
ioctl(fd, WDIOC_SETTIMEOUT, &timeout);
ioctl(fd, WDIOC_GETTIMEOUT, &timeout);
while (1) 
{
        printf("feel dog %d\r\n",count++);
	sleep(1);
}
close(fd);
return 0;
}