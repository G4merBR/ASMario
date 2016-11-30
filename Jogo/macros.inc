; ^_^ Joe(joao.euu@gmail.com)  - 2016


;Inverte Sinal
macro inv a,b{
;a=-b
  fld	  b
  fchs
  fstp a
}
;Soma Float
macro sf a,b,c{
;a=b+c
  fld	  b
  fld	  c
  faddp
  fstp	  a
}
;Subtrai Float
macro suf a,b,c{
;a=b-c
  fld	  b
  fld	  c
  fsubp
  fstp	  a
}
macro sinvoke proc,[arg]		 ; indirectly call STDCALL procedure
 { push eax
   common
    if ~ arg eq
   reverse
     pushd arg
   common
    end if
    call [proc]
    pop eax
    }

;"concatena" strings
macro concat x,y{match xx yy,x y\{x equ xx\#yy\}}