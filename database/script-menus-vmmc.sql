	--==============================MENUS CADASTRO===========================================
	
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M001','','' ,'CON Bem Vindo ao VMMC', 1, null);   --1
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M002','1','', '1. Login', 34, 1);	
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M003','2', '','2. Registar', 4, 1);	

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M004','','firstNames', 'CON Introduza o nome', 6, null);  --4
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M005','0','', '0. Voltar', 1, 4);

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M006','','lastNames', 'CON Introduza o apelido', 8, null); --6
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M007','0', '','0. Voltar', 4, 6);


	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M008','','age', 'CON Digite a sua Idade', 10, null); --8
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M009','0', '','0. Voltar', 6, 8);
 

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M010','','', 'CON Indique o seu numero de telefone', 14, null); --10
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M011','1','', '1. Usar o número corrente', 14, 10);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M012','2','', '2. Introduzir outro numero', 17, 10);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M013','0','', '0. Voltar', 8, 10);
   


	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M014','','', 'CON O seu numero e: {0}', 17, null); --14
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M015','1','', '1. Confirmar', 19, 14); 
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M016','0','', '0. Voltar', 10, 14);

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M017','', 'cellNumber','CON Introduza o outro numero', 19, null); --17
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M018','0', '','0. Voltar', 14, 17);

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M019','','provinceId', 'CON Seleccione a provincia', 21, null); --19
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M020','0', '','{0} 0. Voltar', 17, 19);

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M021','', 'districtId', 'CON Seleccione o distrito', 24, null); --21
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M022','#','', '{0} # Ver mais', 21, 21); -- Verr, o next e o menu corrente porque estamos a fazer paging
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M023','0','', '0. Voltar', 19, 21);
 
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M024','','address',  'CON Preenche o endereco', 26, null); --24
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M025','0','', '0. Voltar', 21, 24);

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M026','', 'hasPartner','CON Tem parceiro', 30, null); --26
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M027','1', '','1. Sim', 30, 26);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M028','2', '','2. Nao', 30, 26);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M029','0', '','0. Voltar', 24, 26);

	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M030','','isConfirmed', 'CON Confirmar os dados: {0}', 34, null); --30
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M031','1', '','1. Sim', 1, 30);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M032','2','', '2. Nao', 1, 30);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M033','0','', '0. Voltar', 26, 30);

 


--==============================MENUS LOGIN===========================================



	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M034','','', 'CON Introduza o Codigo de utente', 36, null);  --34
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M035','0','', '0. Voltar', 1, 34);
	
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M036','','',  'CON Selecione uma opcao', 42, null);  --36
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M037','1','', '1. Marcar Consulta', 43, 36);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M038','2','', '2. Visualizar a consulta marcada', 54,36);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M039','3','', '3. Listar Unid. Sanitarias do distrito', 56,36);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M040','4','', '4. Mensagens Educativas', 62, 36);
    INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M041','5','', '5. Enviar lista de Unid. Sanitarias por SMS', 59, 36); 
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M042','0','', '0. Voltar', 34, 36);
	
	--1 Marcar Consulta
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M043','','clinicId', 'CON Seleccione a U. Sanitaria', 46,null ); --43
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M044','#','', '{0}#. Ver mais', 43, 43); -- Verr, o next e o menu corrente porque estamos a fazer paging
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M045','0','', ' 0. Voltar', 36, 43);
	
    INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M046','','appointmentDay', 'CON Indique o dia da consulta (1 -31)', 48,null); --46
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M047','0','', '0. Voltar', 43, 46);
	
    INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M048','','appointmentMonth', 'CON Indique o mês da consulta', 50,null); --48
 	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M049','0','', '{0} 0. Voltar', 46, 48);
 	
	
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M050','','isConfirmed', 'CON Confirmar a consulta {0}', 1,null); --50
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M051','1','', '1. Sim', 1, 50);
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M052','2','', '2. Cancelar', 1, 50);
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M053','0','', '0. Voltar', 48, 50);
	
	
	
	--2 Detalhes da consulta
	
    INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M054','','','CON Detalhes da consulta {0}', 1,null); --54
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M055','0','', '0. Voltar', 36, 54);
	
	--3Listar unidades Sanitárias do distrito
    INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M056','','','CON Lista de Unidades Sanitarias ', 49,null); --56
	INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M057','#','', '{0} # Ver mais', 56, 56); -- Verr, o next e o menu corrente porque estamos a fazer paging
    INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M058','0','', '0. Voltar', 36, 56);
	
 
 	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M059','','isConfirmed', 'CON Enviar SMS Com lista de Unidades Sanitarias para: {0}', 1,null); --59
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M060','1','', '1. Sim', 36, 59);
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M061','2','', '2. Nao', 36, 59);
	
	
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M062','','', 'CON Seleccione o topico ', 36,null); --62
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M063','1','', '1. Beneficios da circuncisao', 36, 62);
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M064','2','', '2. Como e feita a circuncisao', 36, 62);
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M065','3','', '3. Cuidados apos a circuncisao', 36, 62);
	 INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M066','4','', '4. Efeitos apos a circuncisao', 36, 62);
     INSERT INTO public.menu(code, option,menu_field, description, next_menu_id, parent_menu_id) VALUES ( 'M067','0','', '0. Voltar', 36, 62);
	 
	 
	 INSERT INTO public.info_message( code,description) VALUES ( 'M063','Beneficios da Circuncisao: 1. Redução das infecções urinárias,Redução das infecções do pênis'); 
	 INSERT INTO public.info_message( code,description) VALUES ( 'M063','Beneficios da Circuncisao: 2. Redução do câncer peniano e do câncer do colo do útero nas parceiras'); 
	 INSERT INTO public.info_message( code,description) VALUES ( 'M063','Beneficios da Circuncisao: 3. Redução de Doencas de transmissao sexual incluindo o HIV'); 
	 
	 INSERT INTO public.info_message( code,description) VALUES ( 'M064','Como e feita a circuncisao: A cirurgia é feita através de uma incisão no prepúcio, pele que envolve e protege o órgão genital masculino, o pênis'); 

	 INSERT INTO public.info_message( code,description) VALUES ( 'M065','Cuidados apos a circuncisao: 1. A abstinência sexual de 4 semanas após o procedimento ou até a cicatrização completa da ferida. '); 
	 INSERT INTO public.info_message( code,description) VALUES ( 'M065','Cuidados apos a circuncisao: 2. Usar preservativos após o reinício da atividade sexual, ate que a cicatrização esteja completa (em media 3 meses)'); 
	
	 INSERT INTO public.info_message( code,description) VALUES ( 'M066','Efeitos apos a circuncisao: '); 
	 INSERT INTO public.info_message( code,description) VALUES ( 'M066','Cuidados apos a circuncisao: 2. Usar preservativos após o reinício da atividade sexual, ate que a cicatrização esteja completa (em media 3 meses)'); 
	
	
 
