
#pragma once

#define WIN32_MEAN_AND_LEAN
#define VC_EXTRALEAN

#include <Windows.h>
#include <stdio.h>
#include <string>

HMODULE gamehandle = 0;

int r;

HANDLE hOut, hIn;
void WriteInConsole(const char *fmt, ...)
{

	va_list marker;
	char msg[4096];


	va_start(marker, fmt);
	vsnprintf(msg, sizeof(msg), fmt, marker);
	va_end(marker);

	WriteConsole(hOut, msg, strlen(msg), (LPDWORD)&r, 0);


}

void *FindString(char *p, const char *string, unsigned int len)
{

	for (unsigned int i = 0; i < len; i++)
	{
		if (!(p + i))
			continue;


		if (!memcmp((char*)(p + i), string, strlen(string) + 1))
		{
			return (void*)(p + i);
		}
	
	}




	return 0;
}

void *FindAddrUse(char *p, void*addr, unsigned int len)
{
	for (unsigned int i = 0; i < len; i++)
	{
		if (p[i] != '\x68'&&p[i]!='\xB8')
			continue;


		if (memcmp(&p[i + 1], &addr, 4))
			continue;

		return (char*)(p + i);

	}
	
	return 0;
}


int killjump(char*addr, int size)
{
	unsigned long *oldprot = new unsigned long;
	unsigned long *oldprot_2 = new unsigned long;
	*oldprot = 0;
	*oldprot_2 = 0;

	VirtualProtect(addr, size*4, PAGE_EXECUTE_READWRITE, oldprot);

	memset(addr, 0x90, size);

	VirtualProtect(addr, size*4, *oldprot, oldprot_2);


	delete oldprot;
	delete oldprot_2;
	return 0;
}

void hkSend()
{
	char* packet;
	_asm
	{
		pushad
		pushfd
		MOV packet, EDX
	}
	WriteInConsole("Send: %s\n", packet);
	_asm
	{
		popfd
		popad
	}
}


bool DetourFunc(BYTE* oldFunc, BYTE* newFunc, DWORD len)
{
	BYTE* newMem4base = NULL;
	DWORD dwOld;

	newMem4base = (BYTE*)malloc(5 + len);

	if (newMem4base == NULL)
		return false;

	for (DWORD i = 0; i < (len + 5); i++)
		newMem4base[i] = 0x90;

	VirtualProtect(oldFunc, len, PAGE_READWRITE, &dwOld);

	memcpy(newMem4base, oldFunc, len);
	oldFunc[0] = 0xE8;
	*(DWORD*)(oldFunc + 0x01) = DWORD(newFunc - oldFunc - 5);
	oldFunc[5] = 0xE9;
	*(DWORD*)(oldFunc + 0x06) = DWORD(newMem4base - (oldFunc + 0x5) - 5);
	newMem4base += len;
	newMem4base[0] = 0xE9;
	*(DWORD*)(newMem4base + 0x01) = DWORD((oldFunc + 10) - newMem4base - 5);

	for (DWORD i = 10; i <len; i++)
		oldFunc[i] = 0x90;

	return true;
}


int RemoveClientProtection()
{
	char*clientstr = (char*)FindString((char*)gamehandle + 0x1000, "psrv", 0x54C000);
	char*firstclientstr = (char*)FindAddrUse((char*)gamehandle + 0x1000, clientstr, 0x54C000);


	killjump(firstclientstr + 0xE, 6);
	killjump(firstclientstr + 0x1B, 6);
	killjump(firstclientstr + 0x27, 6);
	killjump(firstclientstr + 0x5D, 2);

	return 0;
}


int main()
{

	AllocConsole();
	hOut = GetStdHandle(STD_OUTPUT_HANDLE);
	hIn = GetStdHandle(STD_INPUT_HANDLE);
	SetConsoleOutputCP(CP_UTF8);


	do
	{
		gamehandle = GetModuleHandleA("NostaleX_p.exe");
	} while (!gamehandle);

	RemoveClientProtection();

	char*senddatastr = (char*)FindString((char*)gamehandle + 0x1000, "NoS0575 ", 0x54C000);
	char*firstsenddatastr = (char*)FindAddrUse((char*)gamehandle + 0x1000, senddatastr, 0x54C000);

	char*senddatacall = (char*)(firstsenddatastr + 0xB4);

	/*

	void* dSend = FindSig((void*)gamehandle, "\x8B\xF2\x8B\xD8\xEB\x04\xEB\x05", 0x54C000, 0);

	if (!dSend)
	{
		MessageBox(0, "Couldn't find send!\n", "k",MB_OK);

		exit(-1);

	}
		
	DetourFunc((BYTE*)dSend, (BYTE*)&hkSend, 14);

	*/

	char commandline[255];
	strcpy(commandline, GetCommandLine());

	if (strlen(commandline) == 0)
	{
		WriteInConsole("Not using a custom server!\n");
		return 0;
	}

	char*fnd = strtok(commandline, "-");

	if (!fnd||strlen(fnd) == 0)
	{
		WriteInConsole("Not using a custom server!\n");
		return 0;
	}

	fnd = strtok(0, "-");

	if (!fnd||strlen(fnd) == 0)
	{
		WriteInConsole("Not using a custom server!\n");
		return 0;
	}

	char customserver[255];
	strcpy(customserver, fnd);

	WriteInConsole("Custom Server: %s\n", customserver);


	FILE*server = fopen(customserver, "rb");

	if (!server)
	{
		WriteInConsole("Couldn't find custom server file!\n");
		return 0;
	}

	fseek(server, 0, FILE_END);
	int fsize = ftell(server);
	fseek(server, 0, 0);

	char buffer[255] = { 0 };

	fread(buffer, sizeof(char), fsize, server);
	fclose(server);

	char server1[23] = { 0 };
	char server2[23] = { 0 };
	char server3[23] = { 0 };

	strcpy(server1, strtok(buffer, "\n"));
	strcpy(server2, strtok(0, "\n"));
	strcpy(server3, strtok(0, "\n"));

	WriteInConsole("Server1: %s\n", server1);
	WriteInConsole("Server2: %s\n", server2);
	WriteInConsole("Server3: %s\n", server3);



	unsigned long old = 0;
	unsigned long old_2 = 0;



	char*foundip = (char*)FindString((char*)gamehandle, "79.110.84.75", 0x54C000);

	if (foundip)
	{

		WriteInConsole("HEY1 __ %x __ %s\n", foundip, foundip);
		VirtualProtect(foundip, 0xFFFF, PAGE_EXECUTE_READWRITE, &old);

		memset(foundip, 0, strlen(foundip));
		memcpy(foundip, server1, strlen(server1));
		foundip[strlen(server1)] = 0;


		VirtualProtect(foundip, 0xFFFF, old, &old_2);


	}


	old = 0;
	old_2 = 0;
	foundip = (char*)FindString((char*)gamehandle, "79.110.84.46", 0x54C000);

	if (foundip)
	{

		WriteInConsole("HEY2 __ %x __ %s\n", foundip, foundip);
		VirtualProtect(foundip, 0xFFFF, PAGE_EXECUTE_READWRITE, &old);

		memset(foundip, 0, strlen(foundip));
		memcpy(foundip, server2, strlen(server2));
		foundip[strlen(server2)] = 0;


		VirtualProtect(foundip, 0xFFFF, old, &old_2);


	}



	old = 0;
	old_2 = 0;
	foundip = (char*)FindString((char*)gamehandle, "121.128.40.4", 0x54C000);

	if (foundip)
	{

		WriteInConsole("HEY3 __ %x __ %s\n", foundip, foundip);
		VirtualProtect(foundip, 0xFFFF, PAGE_EXECUTE_READWRITE, &old);

		memset(foundip, 0, strlen(foundip));
		memcpy(foundip, server3, strlen(server3));
		foundip[strlen(server3)] = 0;


		VirtualProtect(foundip, 0xFFFF, old, &old_2);


	}

//	WriteInConsole("FAGGOTS\n");

	return 0;
}

bool cool = false;


int __stdcall DllMain(void* inst, DWORD reason, LPVOID reserved)
{

	if (!cool)
	{

		cool = true;

			
		main();
		DisableThreadLibraryCalls((HMODULE)inst);

	}

	return 1;
}

