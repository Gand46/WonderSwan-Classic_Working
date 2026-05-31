# WonderSwan-Classic
Open Source PCB clone of WonderSwan PTE-0012A

	Making modifications to make it work with additional adjustments to make it easier to build and program.

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

PCB build
-----
EPM240T100C5 and MX29L3211 Orientation:



All the caps are 100nf SMD 0603 x 11

Flash the EPM240T100C5
-----
To write the firmware to the CPLD, you can use a Pi Pico (https://github.com/thisiseth/pico-usb-blaster) along with the Quartus Programmer available on Intel's website Quartus Prime Programmer and Tools "(QuartusProgrammerSetup-25.1std.0.1129-windows.exe)" plug the pins to the pcb (JTAG MODE):

	GPIO  | I/O | name            | JTAG | AS        | PS
	------+-----+-----------------+------+-----------+-----------
	11    | O   | TCK_DCLK        | TCK  | DCLK      | DCLK
	12    | O   | TMS_nCONFIG     | TMS  | nCONFIG   | nCONFIG
	13    | O   | nCE             | -    | nCE       | -
	14    | O   | nCS             | -    | nCS       | -
	15    | O   | TDI_ASDI        | TDI  | ASDI      | DATA0 
	16    | I   | TDO_CONF_DONE   | TDO  | CONF_DONE | CONF_DONE
	17    | I   | DATAOUT_nSTATUS | -    | DATAOUT   | nSTATUS

NOTE. *if you solder it well to the pcb it will be recogniced on quartus*

1. Connect the Pi Pico USB-Blaster to the PC and to the CPLD JTAG header.
2. Check the JTAG pins: TCK, TMS, TDI, TDO, GND, and VCC reference.
3. Open Quartus Programmer.
4. Go to Hardware Setup and select Pi Pico USB-Blaster or the detected USB-Blaster-compatible device.
5. Click Auto Detect to confirm the CPLD appears in the JTAG chain.
6. Add the programming file, usually .POF for permanent programming.
7. Select Program/Configure and, if available, Verify.
8. Click Start.
9. Wait for 100% Successful.
If JTAG fails, check power, drivers, cable orientation, pin mapping, and solder joints.(free advice the solder joints are the trick)

How to select and patch the rom:
-----
-Check if your game is compatible ( see compatibility list and filter by NO to SRAM as Extra RAM ). 

-IMPORTANT: Modify the 5th byte from the end of the file to 0x00 

		End of the ROM file:

		... AA BB CC DD EE
				↑
				fifth byte from the end

(Added an HTML app to the 'tools' folder for mod 5 byte) 

-Test ROM on a emulator if work continue to the next step

-Pad it to 32Mb with the tools if needed. 

-Flash your SOP44 memory with the file. To do this, you can use MegaBurner by maximaas (https://github.com/maximaas/MegaBurner). I have a compiled version available at (https://github.com/Gand46/MegaBurner_Compiled) with everything you need to get started. 

	Note that the Xgecu T48 is NOT compatible with MX29L3211 (only on the T56 is supported)
	which is why building or using this MegaBurner version is much easier.
-Solder the flash.

-----
Why the 5 Byte?
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
Special Thanks :
-----
X-death25 - For all the hard work,
Zerosquare,
Godzil,
Mellott124,
Up-n-atom,
RedFromNecstasy

*I also added a checksum repair tool for modded ROMs. I don't know if it's required at the moment since the EPM240T100C5 does all the job, but I included it anyway in case someone finds it useful.*

