#!/system/bin/sh

old_baudrate=
`setprop can.baudrate 125000`

while true
do
new_baudrate=`getprop can.baudrate`
case "$new_baudrate" in
   "$old_baudrate")
   ;;
*)

echo "{$new_baudrate}" > /dev/ttymxc0
ifconfig can0 down
ip link set can0 up type can bitrate $new_baudrate triple-sampling on
ifconfig can0 up
old_baudrate=$new_baudrate
   ;;

esac
sleep 1
done
