


typedef unsigned long DWORD;
typedef void* LPVOID;
typedef const void* LPCVOID;
typedef unsigned int*PUINT;

int __stdcall DllMain(void* inst, DWORD reason, LPVOID reserved);

int __declspec(dllexport) GetFileVersionInfoA(char* filename, DWORD handle, DWORD len, LPVOID data)
{
	DllMain(0, 0, 0);

	return 0;
}


int  __declspec(dllexport) VerQueryValueA(LPCVOID block, const char* subblock, LPVOID* buffer, PUINT pulen)
{
	DllMain(0, 0, 0);

	return 0;
}