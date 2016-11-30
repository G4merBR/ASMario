
 ;; Defines
     tamObjeto equ 29
     pcvar equ 22
 ;; FUNCOES
     proc alert titulo,corpo,arg0,arg1
	push	[arg0]
	push	[titulo]
	push	[corpo]
	push	[arg1]
	call	[MessageBoxA]
	ret
     endp
