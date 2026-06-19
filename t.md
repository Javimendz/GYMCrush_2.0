
Modelo Entidad-Relación
------------------------

Entidades Principales
------------------------

Usuario(id, username, email, password, qrToken, tokenExpiraEn, fechaRegistro, createdAt, updatedAt, planActivoId FK)
Perfil(id, nombre, apellidos, telefono, dni, fechaNacimiento, direccion, ciudad, pais, codigoPostal, bio, genero, foto, usuarioId FK)
Role(id, name)
Actividad(id, nombre, descripcion, duracion, sala, precio, categoriaId FK)
Categoria(id, nombre, descripcion)
Horario(id, diaSemana, horaInicio, horaFin, aforoMax, actividadId FK, entrenadorId FK, salaId FK)
Sala(id, nombre, capacidadMax, descripcion, ubicacion, equipamiento, activa)
Reserva(id, fecha, estado, confirmado, usuarioId FK, horarioId FK)
Acceso(id, fechaHoraEntrada, fechaHoraSalida, tipo, usuarioId FK)
Ticket(id, asunto, mensaje, estado, fechaResolucion, fechaCreacion, usuarioId FK)
Notificacion(id, titulo, mensaje, fechaCreacion, leido, tipo, usuarioId FK, ticketId FK)
Chat(id, mensaje, fechaHora, leido, emisorId FK, receptorId FK)
MensajeChat(id, ticketId, contenido, emisor, fechaEnvio, tipo, usuarioId FK)
Salud(id, peso, estatura, nivelActividad, comentario, fechaMedicion, imc, usuarioId FK)
CategoriaDieta(id, nombre, descripcion)
Dieta(id, nombre, tipo, objetivoCalorico, cantidadProteinas, cantidadCarbohidratos, cantidadGrasas, descripcion, categoriaDietaId FK)
Nutricion(id, tipoDieta, fechaGeneracion, nombreDieta, caloriasObjetivo, macronutrientesJson, usuarioId FK, dietaId FK)
ComidaDiaria(id, momento, nombreAlimento, calorias, proteina, carbohidratos, grasas, imagenUrl, fechaRegistro, planId FK)
CategoriaTutorial(id, nombre, descripcion)
Entrenamiento(id, nombre, urlImagen, descripcion, cantidadEjercicios, duracion, intensidad, esGlobal, categoriaId FK, usuarioId FK)
Tutorial(id, titulo, descripcion, musculoObjetivo, equipamiento, urlVideo, duracionMin, esGlobal, categoriaId FK, entrenamientoId FK, usuarioId FK)
Visualizacion(id, fechaCreacion, ultimaActualizacion, completado, progresoSegundos, contadorReproducciones, usuarioId FK, tutorialId FK)
PlanEntrenamiento(id, nombre, descripcion, objetivo, nivel, esGlobal, usuarioId FK)
DetallePlan(id, diaSemana, orden, series, repeticiones, planId FK, entrenamientoId FK)
Rutina(id, fechaAsignacion, orden, completado, series, repeticiones, peso, usuarioId FK, entrenamientoId FK)






Relaciones 
------------


v1: Usuario (1) --- (N) Perfil

Un usuario tiene un perfil (1:1 implementado como 1:N con unique)

v1: Usuario (N) --- (M) Role
v2: Tabla intermedia TUsuarios_Roles(usuarioId FK, roleId FK)

v1: Categoria (1) --- (N) Actividad

v1: Actividad (1) --- (N) Horario

v1: Usuario (1) --- (N) Horario (como entrenador)

v1: Sala (1) --- (N) Horario

v1: Horario (1) --- (N) Reserva

v1: Usuario (1) --- (N) Reserva

v1: Usuario (1) --- (N) Acceso

v1: Usuario (1) --- (N) Ticket

v1: Ticket (1) --- (N) Notificacion

v1: Usuario (1) --- (N) Notificacion

v1: Usuario (1) --- (N) Chat (como emisor)

v1: Usuario (1) --- (N) Chat (como receptor)

v1: Usuario (1) --- (N) Salud

v1: CategoriaDieta (1) --- (N) Dieta

v1: Dieta (1) --- (N) Nutricion

v1: Usuario (1) --- (N) Nutricion

v1: Nutricion (1) --- (N) ComidaDiaria

v1: CategoriaTutorial (1) --- (N) Tutorial

v1: CategoriaTutorial (1) --- (N) Entrenamiento

v1: Entrenamiento (1) --- (N) Tutorial

v1: Usuario (1) --- (N) Entrenamiento (entrenamientos personalizados)

v1: Usuario (1) --- (N) Tutorial (tutoriales personalizados)

v1: Usuario (N) --- (M) Tutorial (a través de Visualizacion)
v2: Tabla intermedia TVisualizaciones(id PK, usuarioId FK, tutorialId FK, fechaCreacion, ultimaActualizacion, completado, progresoSegundos, contadorReproducciones)

v1: PlanEntrenamiento (1) --- (N) DetallePlan

v1: Entrenamiento (1) --- (N) DetallePlan

v1: Usuario (1) --- (N) PlanEntrenamiento

v1: Usuario (1) --- (N) Rutina

v1: Entrenamiento (1) --- (N) Rutina

v1: PlanEntrenamiento (1) --- (N) Usuario (como plan activo)









Modelo Relacional
Tablas
TRoles(id PK, name)  
TUsuarios(id PK, username, email, password, qrToken, tokenExpiraEn, fechaRegistro, createdAt, updatedAt, planActivoId FK)  
TUsuarios_Roles(usuarioId FK, roleId FK)  
TPerfiles(id PK, nombre, apellidos, telefono UNIQUE, dni UNIQUE, fechaNacimiento, direccion, ciudad, pais, codigoPostal, bio, genero, foto, usuarioId FK UNIQUE)  
TCategorias(id PK, nombre UNIQUE, descripcion)  
TActividades(id PK, nombre, descripcion, duracion, sala, precio, categoriaId FK)  
TSalas(id PK, nombre UNIQUE, capacidadMax, descripcion, ubicacion, equipamiento, activa)  
THorarios(id PK, diaSemana, horaInicio, horaFin, aforoMax, actividadId FK, entrenadorId FK, salaId FK)  
TReservas(id PK, fecha, estado, confirmado, usuarioId FK, horarioId FK)  
TAccesos(id PK, fechaHoraEntrada, fechaHoraSalida, tipo, usuarioId FK)  
TTickets(id PK, asunto, mensaje, estado, fechaResolucion, fechaCreacion, usuarioId FK)  
TNotificaciones(id PK, titulo, mensaje, fechaCreacion, leido, tipo, usuarioId FK, ticketId FK)  
TChat(id PK, mensaje, fechaHora, leido, emisorId FK, receptorId FK)  
TMensajes_Chat(id PK, ticketId, contenido, emisor, fechaEnvio, tipo, usuarioId FK)  
TSalud(id PK, peso, estatura, nivelActividad, comentario, fechaMedicion, imc, usuarioId FK)  
TCategorias_Dieta(id PK, nombre UNIQUE, descripcion)  
TDietas(id PK, nombre, tipo, objetivoCalorico, cantidadProteinas, cantidadCarbohidratos, cantidadGrasas, descripcion, categoriaDietaId FK)  
TPlanes_Nutricionales(id PK, tipoDieta, fechaGeneracion, nombreDieta, caloriasObjetivo, macronutrientesJson, usuarioId FK, dietaId FK)  
TComidas_Diarias(id PK, momento, nombreAlimento, calorias, proteina, carbohidratos, grasas, imagenUrl, fechaRegistro, planId FK)  
TCategorias_Tutorial(id PK, nombre UNIQUE, descripcion)  
TEntrenamientos(id PK, nombre, urlImagen, descripcion, cantidadEjercicios, duracion, intensidad, esGlobal, categoriaId FK, usuarioId FK)  
TTutoriales(id PK, titulo, descripcion, musculoObjetivo, equipamiento, urlVideo, duracionMin, esGlobal, categoriaId FK, entrenamientoId FK, usuarioId FK)  
TVisualizaciones(id PK, fechaCreacion, ultimaActualizacion, completado, progresoSegundos, contadorReproducciones, usuarioId FK, tutorialId FK, UNIQUE(usuarioId, tutorialId))  
TPlanes_Entrenamiento(id PK, nombre, descripcion, objetivo, nivel, esGlobal, usuarioId FK)  
TDetalles_Plan(id PK, diaSemana, orden, series, repeticiones, planId FK, entrenamientoId FK)  
TRutinas(id PK, fechaAsignacion, orden, completado, series, repeticiones, peso, usuarioId FK, entrenamientoId FK)  



Url ModeloER draw.io:


https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=Diagrama%20sin%20t%C3%ADtulo.drawio&dark=auto#R%3Cmxfile%3E%3Cdiagram%20name%3D%22P%C3%A1gina-1%22%20id%3D%22aRb18PQsaCeodNmRKsR0%22%3E7V1bc9rIEv41rtp92FOaGV0fCWET6iRx1pfs7nlxySCDskKihIjj%2FfVHGIlLYw82atEdMvuyaCwU0V9PT3%2FfXPpMdSff3%2BXhdPwxG0bJmbSG38%2FU2zMpleO65f8WLQ%2FLFukItWwZ5fFw2SbWDZfxv1HVaFWt83gYzbZuLLIsKeLpduMgS9NoUGy1hXme3W%2Ffdpcl2%2F%2FqNBxFOw2XgzDZbf0zHhbjZasvvXX7%2Bygejet%2FWbjB8i%2BTsL65%2BiWzcTjM7jeaVO9MdfMsK5afJt%2B7UbKwXm2X5fd%2Bf%2BavqxfLo7R44gvXsyg%2Fv%2F26sIm0kvC2BObxpjPppOEkKv%2B3fMDjxeNfri%2BvOxf98%2Bq5STiIxqW1onzbgvW%2F%2F%2FVL%2F%2FbCcq5%2F6%2F7xz%2Fld%2BuaP2zd%2F%2FiZWv2v1vpsvWD1iVjzU5i2NMl18zMv3DNPRovnNXZwk3SzJ8sdbnMVl2Tor8uyfaKN92bC4P0uLdbuyHv%2Br2i%2Brf0qU1%2FfjuIgup%2BXvKhvuS38t28bFJKn%2BnGfzdBgtft3iy8s3%2FRblRfR94%2BWr3%2FYuyiZRkT%2BUt4w30FcV1PcbnlI1VQ9ZvN6yoe4Qwq0awsrOo9Wz12CWHyqD1pcb8B4I9%2Bfexe%2F9D03QlgZtgLYH0BZqG23hKCK0L84%2F9JpgrQzWAGsXYO04zjbWynNosL66qUL55c0C9ssmuNtccbeocBcOAF66AHhHEnXybueq9%2B78ot9pgrjDFXGynh4AwC1vG3DHJ8K7073qf%2Bm%2F7bxtgrdr8N6L9%2FYoHiiiwH7Z%2BdCoa3sGagA1iOXCAVALaRFh%2Ff78oikb8w3cejYm%2FIAL3Be9y97Fl0a9OzBw7yHfvnAg3GQjd7d32UxqsQzcevYtPAF6t28TwX3V7%2F63d9UIbiOt7YNb2dtwryn6seH%2BdH7V%2F73f7XT7558agW4Uth32LWDG5gDULYdoCO%2B%2B7zTr4kZjg2hDjS0AEX0tsBJQsetGtFuwVdbYoO37AG1KYe3mbb931Sg%2FF0ZZ26O0lPTb30JcWYII8eZoG11tT%2F8WTqC2%2B7cXUCVs11cX%2FcbZmpHXXtu%2FpSOokrXzj%2F23nTKodxpOlwijsu3k6BLAbrlwICeDvRzIr66vFnNkjZY%2BCCO27WNm0g621TblSJcG9d6nq4vep87Hfvmhkei2sXDLwF59SwHYJVjhpCTVqI7R0aXR3SDiUFV35Q7gRNH9S%2F%2FyuvOh%2F7%2FGwps0wtu%2B8K4EHNQV1aD%2B%2BUPn000V4xuhbgS4famcdKHI7ntUDL0k6B8%2B9G4W6DdC3Qhxe1O5AEycKtenmie%2Fvuo37OVGhoN4w2XLlrUNt%2FSphNerOErLdjcpn%2FDmNi8%2FjRaffilt0RG%2FNnKDw%2FW5cTa5nc9acoLjrGu1d50ALmsVINILl2yZ4ywepeEwu%2Bk84QmfSk%2F42MwTDtftTtMT4PpHZ8cTqBhdtzTPIiLcXM%2Fmz%2FhCw6hwuJp3mr4gLEjv4ZI5eme4yJJ2nOFwke9ndQZFxQa6STiL7%2BJB%2BEyy8KmRJ6jDdb%2FT9ISdjNHfzhjJcoXPeTbKw0lLbnC4GHiabrBvPXVAtTynk9xG%2BaglLzhcHDxNL4Dz%2Bi5Yt0O3zLo%2FKYEpniOQDd3gcLXw53CDwIKZItWG2Hdllpi3FAwOVw9%2FCi%2BQLkwRBdXs0EUUJvG%2FLbnB4aLiz%2BEGvtxOEOmCwUU0iksbtuQHRlXUEgUB1%2FSKgGqFZ%2Bc2bykxMHrimU5ZFj44FYVuo06biYFREvXpobLBJJPlEHnBRTSIb1uKBUZC3EMS4I4eMi%2FozW56k3iW5a04gm0URK10JD0FwoGgmlcoHaGMCNG0aMsVjIp4pp1v9CSfkaFFrmAbHVHLFcprMDbYVFyh3dkl2yiJetLo%2BGDbUEC2EbSac27HD4yW%2BEo%2FcKmI42U8mrfkBEZJ1KsHHjh9j3AzWTaZZm3FAiMkavmCUO6OG5xmcmDURH2WqOD2I7pTItp1BCMo6h1hZx%2BaI4j2m%2FbTQTJ%2FaGlgMIqiVlGUNlitTLf%2FtM0JZ8foiXo3gMOC9MjWog0G0bCdYOAYKXFPMPC4eMGXeDZvMRwYLVGfHATwmEBFRRd6s5vSF4rsppe24wpGTTzTTS8oAQQEuvMKunnUUjgwSqJWRJI2PFrQpVqi3Kqi7Bgxcc%2BwAJci0S1H021yRfAEoydqR4XF7ML2qEC2halX3jwvWhoYjJqoJwwWXI%2FkUgWEN%2BEsHIatZYlGTdTGAxHA0%2BsElaz8OQnTm86giL9lrZyE4RhFUT80uDAmOC4RYajxn9fwl8BWTeXzV61NvME9WFIq742ns%2BiH9oYnDseB5xsKuCpJUu15n5ffWP6pAdwHC0cnCjdclyohQ7Cp1pxEkzBOGmF9sDJ0oljvVPKE9aECqqmjaTib3Wf5sBHcB4tAJwo3JP9wplAEVJH8GOP6wVLQiXoDHNctuEOVrFZ3WmbVebNR%2FWC150TBhjRfAHYnVrHg6KP6MGwW5Q9WdE4UajioWzDKk0F9lxVZI6gP1mxOFGob9mqwpVQoqlz9GAP6wbLNiXoDHNBdB3oD1ZETzQd07%2BDlXicKNhzQHQg22aq%2FI3R9z2h0%2Bq4vweYAh2ouD6HnG4FO3%2FMtcMAUmT53jI5vFDx9x1eAxJNV%2FEbo%2BEa%2B03d8BSZiAkWU3g2j2SCPp4M4SxsBbhS6M%2F1UDJDnvROWaz2j4GkjvbDBXnBBVgocIdQbDU8b6nc2%2BwppEcX6QViaIm6o2XpGyNNGemG725WhhaJann2MUG%2BEPH2oh0vxhKBakzmMw8toEqZhE7x9o%2BXpe78H9moJsnOfxlke9tO4zOwbAW7kuj2Ag1KidB38COHeN4KeXsPx4YStRSXlR7MiHDbr%2Bkaw02b2yoKxnqxWyDG6vpH09JmeB1balw1EPO8uGozDXlrk4bBZsmdEvTPtPpvVKrsacjLJ%2FhgBwKh6%2BgCgdorKU63ZCGfztNlyLd%2BoenpVTwQ2AJvqnGekGRzf6Hp6oicEQJys8scxgr3R9fTB3oGlwcjKRRZxMU8aBfvAiHpnumXYwoZrcwOqYH%2BErh8YxU%2Bv8QQ7JR4kUZ43idJZ%2BLXR9G1gFL0z3UY7tXOohnXCHC8wkp%2B%2B70N9l%2B7srWk0azboGz1PO%2BjLwAYFG8j2XoVJMc8bKXmBUfL2gL1TmMEhGtOPEeWNkreH3AElT1k%2F7h6MwCh5eiVv5%2Bh9MrCP0fWNyqfv%2BruHblNN4CN0fSPi6bs%2BnLKlA%2FsIXb86%2B8u4w7N937W4ZPzN%2B76wjIyn7%2FyOgGhTKfhH6fxG59N3fmFBBki1Q2tS%2FjMNp%2B%2BFZYS8M52sG8AKO2SFGI%2FS%2BY3Wp%2B380gEF3BXZydkYI78R%2B7Qjv3Qg47dPeDpXWEbt04%2F8AainQld0D6PzG7lP3%2FktH4R6sg1aR%2Bn8Ru%2FbM%2FKDtF8Jqs7ffB2XsIzgd6ad5nPBqj2lTljrrzYiGnd4rvMrtRwtNwsrUm7ZWZTUa7qMWwij%2B22Dvqp5saqhCA7kkKe8Y1cII%2FzpMwAPJIRlw4%2Bb%2Fguj%2B%2BnTfxfO75KVyThK5zfCn77zB%2BDoXeVRbeqZNT6bRQgj%2FJ3p13KDzq9cnyjfO0rnN8KfXvjz4YZdn6pM4iCbTJOo6YEdQhjxbxtxWA1xp%2F4dEuIra%2B%2FBp1pxEA1H0RYiT4GUzfNBpHmadHfQjNJhJ8%2Bz%2B%2FIqzdJo28IQwjVUy7%2F8WZlNruxfhPkoKnQ%2FqMIpTOaruPYUSnmUhEX8bfs3v8LiLzVvgGlez5h3%2B23qroJjXp%2FcvDYz8wpM8wbGvMC8EtG89VBDaF6HmXkVpnl3E5Vjm9dlZl4b07yS3LweM%2FM6mOZV5OZFzxwev1r%2BhPBh44ZpFqfFbOPJnxcNG%2Bm2hOl2vZl6DdbymWvoVi%2FXBE0XE02bHE2fWWfxMM3rkJuXW5qNSRKVIYnQvJgkURmSCN5GYZJERU8SBTf7YrJERc8SuZkXkyXa9CyRm3kxWaJNzxK5mReTJdr0LJGbeTFZos2AJTLTkBQmb7PpeZtgJiIpTOJmG%2BIGzYtJ3GwGxI2ZSKcwmZvNgLkFvOxrY1I3mwF142ZfTOpm01O3ej6YjX0xuZtjuBs0LyZ3cwx3g%2BbF5G6O4W7QvJjczaHnbpKbfTG5m8OAuzEzLyZ1c%2BipW71chI19MbmbQ8%2Fd2OVmmNzNYcDdeJnXwaRuDj114%2Ba%2BdbqKY18G1I1Z%2BK3zVRT7uoZbQPNiUjfXcAtoXkzq5tJzC27mxaRurqEW0LyYzM1lQC2YmReTubkMmAUz82ISN5eeWHAzLyZvcxkQC17mdTF5m0vPK5jRipoJoJjXo58R4mZeTNbm0bM2ZsuhXEzW5tGzNm7mxWRtHj1rY7bYzMVkbR49a%2BNmXkzW5tGzNm7mxWRtHj1rY7aUz8VkbR49a%2BNmXkzW5tGzNmbm9TBZm0fP2pjtL%2FYwWZtPz9q4mReTtfn0rI2beTFZm0%2FP2pgtovYwWZtPz9q4mReTtfn0rE0wW0jiYdI2n562sbMvJm%2Fz6Xkbt%2BMHPEzi5tMTN3b2xWRuPj1z42ZfH5O6%2BfTUTUhm9sXkbgE9d2NnX0zyFtCTN8FsStPHZG8BPXtjZ19M%2BhbQ0zfB7JRbH5O%2FBQz4Gzf7YvK3gAF%2F42ZfTP4WMOBvzBY9%2BJj8LWDA37jZF5O%2FBQz4G7N5%2BQCTvwUM%2BBs3%2B2Lyt1W1FXOI0drAmAROWAwYHDcDYzI4YTGgcMwmOANMCicsBhyOm4ExOZywGJA4ZpOcASaJExYDFsfNwJgsTlj0NI7beQQBJo0TFj2PY2dgTB4nLHoiV78sEwOvKsMgGZieybEzMCqTE%2FRMTkpmBkZlcvVEuTHw2sCoTK6eyTWn7qwNjMrkBD2TY2dgVCYn6Jmc5DVdJC1UJifomRw7A6MyOcGAyfGaMJIWKpMTDJgcNwNjMjkGNdeZJWkCtSg4vfsyyyAEJotjUHOdGUmuScGp1Fzn5r2oRcHpJQhek%2FVSoBYFpxcgeM3DSYFaFJxefuBmXtQq3fTiAzfzolbpppceeE1xSoFapZteeOBmXkzWxqEIOq%2F5TSlRy3TT0zZm%2BwxXTz6ZMujMZAeJSdw4lEHnZl%2FUQt30zI3ZPjgpUSt101M3dvZFLdVNz92Y7dOSErVUNz15Y7YPQ0rUUt307I3ZLgEpUWt109M3dvbF5G8caqEzU3cUaq1uev7GbP2vVJj8jUEtdG6rU2s5%2FGRqoXOzL2qxbnr%2Bxi4%2BoFbrpudv7PwXtVw3PX%2BTzPQdhVqum56%2FsbMvar1uev7GbWW1Qq3XTc%2FfuK37VZj8jUM9dGb2tVELdjPgb8xW%2FdaS%2BMkUROdmX0z%2BxqAgOrP010YtIvBEeCife1ldZnkxzkZZGia9deubPJunw2jxkgu7ru%2F5kGXTCoavUVE8XMb%2FLh4SzotsG6Toe1z8tfj6f5zq6u%2FqYYvPb79vXjzUF2lpvb82L%2F7evFh%2F6fGq%2FtYLIV6aAR%2FS61mUn99%2BjQZF%2Bb0kvI2S2oHcpHyfN8P4W%2FlxVDy%2B6bLpNoct5TM37vsYpbPwa3TTHYdFg8csf0oaTqrf1n3fuarcKAkH0ThLhlE%2B2%2Frde9zS3jBh5chPeeTa0WbjcLr4mJfmCdPRo2vdxUmy7rzO4vKJTu0sGxb3Z2nxVGdftNdOvPC4%2B3FcRJfT8LEz3OfhdNshtz266u9RXkTfN15%2B1z3GUTwaV7%2BvVl3vqwBTXtUHktUPceqqjQ%2FLBinq2YFXuFR5ueFVWi8rAQMoF3ExT7JmODuH4lzeG09nbaFsUaHsQJTr4wtWKNcN7aB8Fw3GYS%2F9FmdtoO0atPVo%2B6BPr5bPtYP2oDRClMbDVsD2DNhasF0FurYVoID90swPU9iQbc5Mr5O1ZYb30nStTgzrJPHvrSTx6cTwhSmetNFpfvXVz1mcFhteInYGAAW8ZAlO9b21ozzxqLrmUv0oBceS5e%2FeedSjz61%2BUxO2gbpFts3Z5KdJhdD6zqF%2Bys7npA8jk3%2Boz0kf%2BNwqDB7L5xzUfcNtrhB5zn%2FEC%2BPcawgwP5%2BTwFEEHA1f7nMK%2BpyL5XPPJVKLjDl8gkD%2FUmLX%2BfTry1jzuyiN8nCXc68e0iAbczZzltcR6nE2uZ3PfuhszN7NxlyQjXku9D8cOv3SKIUpc7Y4y7%2BKUWIzQm0ErIZjXL1CofV4EzhwjPMOjTceTNF2HtX6GIep4so2V4kc6j%2FrMU4EW%2Bm8bdfp2jNJWXnxOcrj0sZRznTwk5YNk%2FyDEy4v2Pco9MGvW8bzuBy7nh7%2FZJfF%2BHe4oHya419dnLt%2BiF1zs2f9pt3xD3MZlmxzH8IqEr1aVWh9TPMVnBKoB4ZXhxFHthZGXuoRmAvHZJsrmzZ4m3eQQFUOav6WN3muamtQw9%2B%2F%2FIwHuTvzU%2F7Bzrh3eDzYGcvLPMuKzdvLADz%2BmA0Xo1vv%2Fw%3D%3D%3C%2Fdiagram%3E%3C%2Fmxfile%3E




Introduccion


orden para el flujo de la demo: login -> qr -> chat -> horario -> reserva ->dieta -> Entrenamiento