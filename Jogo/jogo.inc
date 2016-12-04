
      ;; DEFINES
      tamObjeto equ 29
      ;; Macro Render
      macro render obj,tam{

	mov	eax,0
	push	eax	;Salva EAX
	@@:
	pop	eax	;Load EAX
	cmp	eax,[tam]
	je	@f

	push	eax

	imul ebx,eax,tamObjeto

	pop	eax
	inc	eax
	push	eax

	;Nova Logica
	invoke	glBegin,GL_QUADS       ;Desenha Quadrado
	invoke	glColor3f,[obj+ebx+17],[obj+ebx+21],[obj+ebx+25]  ;Cor RGB(R,G,B)

	invoke	glVertex2f,dword[obj+ebx],dword[obj+ebx+4] ;Verice 1 Pos(x,y)
	;aux=x+sx
	sf	[aux],dword[obj+ebx],dword[obj+ebx+8]
	invoke	glVertex2f,[aux],dword[obj+ebx+4] ;Vertice 2 Pos(x+sx,y)
	;aux1=y+sy
	sf	[aux1],dword[obj+ebx+4],dword[obj+ebx+12]
	invoke	glVertex2f,[aux],[aux1];Vertice 3 Pos(x+sx,y+sy)
	invoke	glVertex2f,dword[obj+ebx],[aux1];Vertice 3 Pos(x,y+sy)
	invoke	glEnd

	jmp	@b

	@@:
     }
     macro casoscolisao wheretogo {
	;Fim da Fase
	cmp	ah,2
	jne    @f
	stdcall tocamusica,_looney
	mov    [screen],3
	jmp	wheretogo
	@@:


	;Inverter
	cmp	ah,3
	jne    @f
	mov   [toinvert],1
	jmp    wheretogo
	@@:

	;Boost
	cmp	ah,4
	jne    @f
	mov   [boost],1
	jmp    wheretogo
	@@:

     }

      .nextframe:

	 cmp	 [screen],1
	 je	.ingameclock
	 invoke glLoadIdentity
	 jmp	 .renderSection

	.ingameclock:
	local aux:DWORD
	local aux1:DWORD
	local aux2:DWORD
	local aux3:DWORD

	;Pontuacao
	;xor	  edx,edx
	;mov	  eax,[player.x]
	;mov	  ebx,120
	;div	  ebx
	;mov	 [aux],edx
	;mov	 [aux1],0.0
	;fld	 [aux]
	;fld	 [aux1]
	;fcompp
	;fstsw ax
	;fwait
	;sahf
	;jne	 .atualiza

       ; mov	 ah,[pontuacao]
       ; add	 ah,1
	;mov	 [pontuacao],ah

       ;.atualiza:
       ; mov	 ah,[pontuacao]
       ; mov	  bh,0
	;.separaloop:
	;cmp	  ah,10
	;jb	  .fimseparacao
	;inc	  bh
	;sub	  ah,10
	;jmp .separaloop
	;.fimseparacao:
	;add	 ah,48
	;add	 bh,48
	;mov	 [_ptitulo+11],bh
	;mov	 [_ptitulo+12],ah
	;invoke SetWindowText,[hwnd],_ptitulo

	;Aumenta Velocidade com o Tempo, aplica boosts e inversoes
	mov	 [aux],0.000005
	cmp	 [toinvert],1
	je	 .inversao
	cmp	 [boost],1
	je	.salvaboost
	cmp	 [boost],2
	jae	 .boost
	jmp	 .fimboost

	.salvaboost:
	 mov	 [aux1],0.0
	 fld	 [oldv]
	 fld	 [aux1]
	 fcompp
	 fstsw ax
	 fwait
	 sahf
	 jne	 .boost
	 mov	 eax,[player.v]
	 mov	 [oldv],eax
	.boost:
	 cmp	 [boost],50
	 ja	 .deboost

	 sf	 [oldv],[oldv],[aux]
	 mov	 [aux],0.001
	 inc	 [boost]

	 jmp	 .fimboost
	 .deboost:
	 mov	   [aux],0.001
	 suf	   [player.v],[player.v],[aux]
	 mov	   [aux],0.00
	 fld	   [player.v]
	 fld	   [oldv]
	 fcompp
	 fstsw ax
	 fwait
	 sahf
	 jb	 .fimsub

	 mov	 eax,[oldv]
	 mov	 [player.v],eax
	 mov	 [boost],0
	 mov	 [oldv],0.0
	.fimboost:
	sf	 [player.v],[player.v],[aux]
	jmp	 .fimsub


	.inversao:
	mov	[aux1],-0.012
	fld	[aux1]
	fld	[player.v]
	fcompp
	fstsw ax
	fwait
	sahf
	jbe	   .subnormal
	mov	  [aux],0.0001
	suf	  [player.v],[player.v],[aux]
	jmp	  .fimsub
	.subnormal:
	suf	  [player.v],[player.v],[aux]
	.fimsub:
	jmp    .godown ;aplica Gravidade

	.ngrav:
	;Atualiza Bordas e Move Camera

	sf	[minx],[minx],[player.v]
	sf	[maxx],[maxx],[player.v]


	inv	[aux],[player.v]
	invoke	glTranslatef,[aux],0.0,0.0;move camera
	call	.goright   ;move player

      .renderSection:



	invoke	glClear,GL_COLOR_BUFFER_BIT ;Limpa Tela



	;Logica das Telas
	cmp	[screen],1
	je	.ingame
	cmp	[screen],2
	je	.morto
	cmp	[screen],3
	je	.fim


	.menu:
	render screen1,screen1size
	jmp	.endrenderplayer

	.morto:
	render screen2,screen2size
	jmp	.endrenderplayer

	.fim:
	render fimscreen,fimsize
	jmp	.endrenderplayer

       .ingame:
       ;Morreu?
	fld	[player.x]
	fld	[maxx]
	fcompp
	fstsw ax
	fwait
	sahf
	jbe	.die

	fld	[player.x]
	fld	[minx]
	fcompp
	fstsw ax
	fwait
	sahf
	jae	.die

	fld	[player.y]
	fld	[maxy]
	fcompp
	fstsw ax
	fwait
	sahf
	jbe	.die

	fld	[player.y]
	fld	[miny]
	fcompp
	fstsw ax
	fwait
	sahf
	jae	.die

	jmp    .live
	.die:
	 stdcall tocamusica,_slap
	mov	[screen],2
	jmp	.morto
	.live:
	;Desenha Chao
	mov	eax,0
	push	eax	;Salva EAX
	.renderfloor:
	pop	eax	;Load EAX
	cmp	eax,[chaosize]
	je	.endrenderfloor

	push	eax
	mov	ebx,29	;Tamanho do Objeto
	mul	ebx
	mov	ebx,eax

	pop	eax
	inc	eax
	push	eax

	;Colisao
	;Ignorar
	mov	ah,[chao+ebx+16]
	cmp	ah,0
	je     .fimcolisao



	;Eixo x
	;one.Position.x + one.Size.x >= two.Position.x &&
	fld	[player.px]
	fld	[player.sx]
	faddp
	fstp   [aux]

	fld    [aux]
	mov    eax,[chao+ebx]
	mov    [aux1],eax
	fld    [aux1]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao
	;two.Position.x + two.Size.x >= one.Position.x
	mov	ecx,[chao+ebx]
	mov	[aux],ecx
	fld	[aux]
	mov	ecx,[chao+ebx+8]
	mov	[aux1],ecx
	fld	[aux1]
	faddp
	fstp	[aux]
	fld    [aux]
	fld    [player.px]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao
	;Eixo Y
	;one.Position.y + one.Size.y >= two.Position.y &&
	fld	[player.y]
	fld	[player.sy]
	faddp
	fstp   [aux]

	fld    [aux]
	mov    eax,[chao+ebx+4]
	mov    [aux],eax
	fld    [aux]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao
	;two.Position.y + two.Size.y >= one.Position.y
	mov	eax,[chao+ebx+4]
	mov	[aux],eax
	fld	[aux]
	mov	eax,[chao+ebx+12]
	mov	[aux],eax
	fld	[aux]
	faddp
	fstp	[aux]
	fld    [aux]
	fld    [player.y]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao

;	;Colidiu
	mov	ah,[chao+ebx+16]
       ;Fim da Fase
       casoscolisao .fimcolisao
	mov    [player.col],1

	.fimcolisao:


	invoke	glBegin,GL_QUADS       ;Desenha Quadrado
	invoke	glColor3f,[chao+ebx+17],[chao+ebx+21],[chao+ebx+25]  ;Cor RGB(R,G,B)


	invoke	glVertex2f,[chao+ebx],[chao+ebx+4] ;Verice 1 Pos(x,y)
	;aux=x+sx
	mov	ecx,[chao+ebx]
	mov	[aux],ecx
	fld	[aux]
	mov	ecx,[chao+ebx+8]
	mov	[aux],ecx
	fld	[aux]
	faddp
	fstp	[aux]
	invoke	glVertex2f,[aux],[chao+ebx+4] ;Vertice 2 Pos(x+sx,y)
	;aux1=y+sy
	mov	ecx,[chao+ebx+4]
	mov	[aux1],ecx
	fld	[aux1]
	mov	ecx,[chao+ebx+12]
	mov	[aux1],ecx
	fld	[aux1]
	faddp
	fstp	[aux1]
	invoke	glVertex2f,[aux],[aux1];Vertice 3 Pos(x+sx,y+sy)
	invoke	glVertex2f,[chao+ebx],[aux1];Vertice 3 Pos(x,y+sy)
	invoke	glEnd

	jmp	.renderfloor

	.endrenderfloor:


	 cmp	 [player.col],0
	 je	 .naocolidiu

	 mov	eax,[player.x]
	 mov	[player.px],eax
	 mov	eax,[player.y]
	 mov	[player.py],eax
	 mov	[player.col],0
	.naocolidiu:
	 mov	eax,[player.px]
	 mov	[player.x],eax
	 mov	eax,[player.py]
	 mov	[player.y],eax

	;Desenha Player


	mov	eax,0
	push	eax	;Salva EAX
	.renderplayer:
	pop	eax	;Load EAX
	cmp	eax,[playersize]
	je	.endrenderplayer

	push	eax
	mov	ebx,29	;Tamanho do Objeto
	mul	ebx
	mov	ebx,eax

	pop	eax
	inc	eax
	push	eax


	 ;Nova Logica

	invoke	glBegin,GL_QUADS       ;Desenha Quadrado
	invoke	glColor3f,[playermodel+ebx+17],[playermodel+ebx+21],[playermodel+ebx+25]  ;Cor RGB(R,G,B)

	fld	[player.x]
	mov	ecx,[playermodel+ebx]
	mov	[aux2],ecx
	fld	[aux2]
	faddp
	fstp	[aux2]
	fld	[player.y]
	mov	ecx,[playermodel+ebx+4]
	mov	[aux3],ecx
	fld	[aux3]
	faddp
	fstp	[aux3]
	invoke	glVertex2f,[aux2],[aux3] ;Verice 1 Pos(x,y)
	;aux=x+sx

	fld	[aux2]
	mov	ecx,[playermodel+ebx+8]
	mov	[aux],ecx
	fld	[aux]
	faddp
	fstp	[aux]
	invoke	glVertex2f,[aux],[aux3] ;Vertice 2 Pos(x+sx,y)
	;aux1=y+sy

	fld	[aux3]
	mov	ecx,[playermodel+ebx+12]
	mov	[aux1],ecx
	fld	[aux1]
	faddp
	fstp	[aux1]
	invoke	glVertex2f,[aux],[aux1];Vertice 3 Pos(x+sx,y+sy)
	invoke	glVertex2f,[aux2],[aux1];Vertice 3 Pos(x,y+sy)
	invoke	glEnd

	jmp	.renderplayer

	.endrenderplayer:
       ; Texto
	;invoke    glRasterPos2i,[player.x],0
       ; invoke  wglUseFontBitmapsA,[hdc],0,255,1000
      ;  invoke glListBase,1000
       ; invoke glCallLists,24, GL_UNSIGNED_BYTE,_pcorpo


	invoke	SwapBuffers,[hdc]
	xor	eax,eax
	jmp	.finish

  .inverteg:
       push ebp
	mov ebp, esp
	cmp	[screen],1
	je	.skipreset
	;Reset
	mov	[player.x],0.0
	mov	[player.y],0.0
	mov	[player.px],0.0
	mov	[player.py],0.0
	mov	[minx],-1.3
	mov	[miny],-1.3
	mov	[maxx],1.3
	mov	[maxy],1.2
	mov	[player.y],0.0
	mov	[player.v],0.012
	mov	[player.g],0.032
	mov	[player.col],0
	mov	[toinvert],0
	mov	[boost],0
	mov	[oldv],0.0
	mov	[screen],1
	jmp	.finverteg
	.skipreset:
	mov	[screen],1
	cmp	[player.gcount],1
	jne	.finverteg
	stdcall tocamusica,_invert
	mov	[player.gcount],0
	fld	[player.g]
	fchs
	fstp	[player.g]
	.finverteg:
	mov esp, ebp
	pop ebp
	ret
   .goup:
	push ebp
	mov ebp, esp

	fld	[player.y]
	fld	[player.v]
	faddp
	fstp	[player.py]

	mov esp, ebp
	pop ebp
	ret
   .invert:
	 mov	[toinvert],1
	 ret
   .godown:

	fld	[player.y]
	fld	[player.g]
	fsubp
	fstp	[player.py]
	;Colisao

	mov	eax,0
	push	eax	;Salva EAX
	.loopele:
	pop	eax	;Load EAX
	cmp	eax,[chaosize]
	je	.endloopele

	push	eax
	mov	ebx,29	;Tamanho do Objeto
	mul	ebx
	mov	ebx,eax

	pop	eax
	inc	eax
	push	eax

	;Colisao
	;Ignorar
	mov	ah,[chao+ebx+16]
	cmp	ah,0
	je     .fimcolisao1

	;Eixo x
	;one.Position.x + one.Size.x >= two.Position.x &&
	fld	[player.x]
	fld	[player.sx]
	faddp
	fstp   [aux]

	fld    [aux]
	mov    eax,[chao+ebx]
	mov    [aux1],eax
	fld    [aux1]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao1
	;two.Position.x + two.Size.x >= one.Position.x
	mov	ecx,[chao+ebx]
	mov	[aux],ecx
	fld	[aux]
	mov	ecx,[chao+ebx+8]
	mov	[aux1],ecx
	fld	[aux1]
	faddp
	fstp	[aux]
	fld    [aux]
	fld    [player.x]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao1
	;Eixo Y
	;one.Position.y + one.Size.y >= two.Position.y &&
	fld	[player.py]
	fld	[player.sy]
	faddp
	fstp   [aux]

	fld    [aux]
	mov    eax,[chao+ebx+4]
	mov    [aux],eax
	fld    [aux]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao1
	;two.Position.y + two.Size.y >= one.Position.y
	mov	eax,[chao+ebx+4]
	mov	[aux],eax
	fld	[aux]
	mov	eax,[chao+ebx+12]
	mov	[aux],eax
	fld	[aux]
	faddp
	fstp	[aux]
	fld    [aux]
	fld    [player.py]
	fcompp
	fstsw ax
	fwait
	sahf
	ja     .fimcolisao1

;	;Colidiu

	 casoscolisao .fimcolisao1

	 mov	[player.col],1
	.fimcolisao1:
	jmp    .loopele
	.endloopele:
	 mov	 ah,0
	 cmp	 [player.col],0
	 je	 .naocolidiu1


	 mov	eax,[player.y]
	 mov	[player.py],eax
	 mov	[player.col],0
	 mov	ah,1
	.naocolidiu1:
	 mov	[player.gcount],ah
	 mov	eax,[player.py]
	 mov	[player.y],eax


	jmp .ngrav

   .goright:
	push ebp
	mov ebp, esp

	fld	[player.x]
	fld	[player.v]
	faddp
	fstp	[player.px]

	mov esp, ebp
	pop ebp
	ret
   .goleft:
	push ebp
	mov ebp, esp

	fld	[player.x]
	fld	[player.v]
	fsubp
	fstp	[player.px]

	mov esp, ebp
	pop ebp
	ret