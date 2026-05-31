# WonderSwan-Classic
Open Source PCB clone of WonderSwan PTE-0012A

![Swan_pcb1](https://github.com/X-death25/WonderSwan-Classic/blob/main/gfx/WS_Classic.PNG)
![Swan_pcb2](https://github.com/X-death25/WonderSwan-Classic/blob/main/gfx/WS_Classic_02.jpg)
![Swan_pcb3](https://github.com/X-death25/WonderSwan-Classic/blob/main/gfx/DSC_0121.png)
![Swan_pcb4](https://github.com/X-death25/WonderSwan-Classic/blob/main/gfx/Dsc_0118.jpg)
![Swan_pcb5](https://github.com/X-death25/WonderSwan-Classic/blob/main/gfx/DSC_0124.png)

What is it ?
-----

This is a clone of classic Wonderswan PCB.
It can be used for developpement or to clone compatible games / Homeberew.

Product Feature :
-----

    Support ROM up to 32Mb 
    CPLD clone of Bandai 2001 based on famous EPM240T100C5
    Can be Rewritable with third party adapter ( Sanni Cart Reaer or your own flasher )

Cartridge supported :
-----
    
    Classic 32Mb cartridge ( no RAM  )
    No EEPROM support Yet   
     
Compatible & Tested Memory :
-----
    Macronix MX29L3211 
      
How to use it :
-----
-Check if your game is compatible ( see compatibility list and filter by NO to SRAM as Extra RAM ). 

-IMPORTANT: Modify the 5th byte from the end of the file to 0x00 

		End of the ROM file:

		... AA BB CC DD EE
				↑
				fifth byte from the end

(also add a htlm app to make this mod in tool folder) 

-Test ROM on a emulator if work contunye

-Pad it to 32Mb with the tools if needed. 

-Flash your SOP44 memory with the file. to flash it use megaburner by maximaas (https://github.com/maximaas/MegaBurner) i have a version compiled on (https://github.com/Gand46/MegaBurner_Compiled) with all to work

-Solder the flash. 
-----
for write the firmware on the clpd you can use a pi pico (https://github.com/thisiseth/pico-usb-blaster) and the quartus programmer on intel web
-----
The explanation of the modification of the 5 byte is because of this:

According to the WonderSwan technical table:

00 = no save memory
01 = 64K SRAM
02 = 256K SRAM
03 = 1M SRAM
04 = 2M SRAM
10 = 1K EEPROM
20 = 16K EEPROM
50 = 8K EEPROM
-----

Where to buy it :
-----
You can build it your self or buy me a ready to use cartridge

Special Thanks :
-----

Zerosquare,
Godzil,
Mellott124,
Up-n-atom,
RedFromNecstasy

