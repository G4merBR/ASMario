
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

     proc randd max
	invoke	time
	mov	 edi,eax
	invoke	  srand
	invoke	  rand
	cdq
	idiv	 [max]
	mov	eax,edx
	ret
      endp
      proc tocamusica nome
      ;13 - repete
	invoke	PlaySound,[nome],NULL,5
	ret
       endp