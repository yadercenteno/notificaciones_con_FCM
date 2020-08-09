<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<title>Ejemplo de envío de mensaje con Firebase</title>
	<style>
		h1   {
			color: #4169E1;
		}
		
		.etiqueta {
			float: left; 
   			width: 25%; 
   			font-weight: bold;
		}

		.bordes {
			border-style: dotted;
			border-color: #4169E1;
			padding: 0.5em 0.2em; 
			width: 80%;
		}

		.grupos_campos {
			clear: left; 
			margin: 0.2em; 
			padding: 0.1em;
		}   		
	</style>
</head>
<body>
	<h1>Ejemplo de envío de mensaje con Firebase</h1>
	<?php 	
	// Se ha enviado el formulario
	if (isset($_REQUEST['enviar'])) {	

		$ch = curl_init("https://fcm.googleapis.com/fcm/send");

		//Esto se obtiene de Firebase en la Configuración, dentro de la pestaña "Cloud Messaging", ahí está ese Server Key		
		$serverKey = "Aquí va el Server Key";
		
		// Este es el tema al que se suscriben la App en Android, el nombre puede ser cualquiera, pero debe ser igual aqui y en la App Android
		$token = "/topics/Mensaje";

		// Recibo los datos del formulario
		$titulo = $_REQUEST['titulo']; 
		$cuerpo = $_REQUEST['cuerpo'];
		$fecha = $_REQUEST['fecha'];
		$lugar = $_REQUEST['lugar'];
				
		// Aqui almacenaré los errores que aparezcan
		$errores = array();
		
		// Inicio la validación de los datos recibidos
		if ($titulo=="" || $titulo==null) {
			$errores[] = "Tenés que ingresar un Título para la Notificación";	
		}
		if ($cuerpo=="" || $cuerpo==null) {
			$errores[] = "Tenés que ingresar un Cuerpo para la Notificación";	
		}
		if ($fecha=="" || $fecha==null) {
			$errores[] = "Tenés que ingresar una Fecha para la Notificación";	
		}
		if ($lugar=="" || $lugar==null) {
			$errores[] = "Tenés que ingresar un Lugar para la Notificación";	
		}

		if (count($errores)>0) {
			// Si hubo errores, los muestro en pantalla
			echo "<p>NO se puede enviar la Notificación debido a los siguientes errores:</p>";
			echo "<ul>";
			foreach($errores as $error) {			
				echo "<li>".$error."</li>";
			}
			echo "</ul>";			
			echo "[ <a href='notificacion.php'>Volver</a> ]";
		}
		else {
			//Creamos el arrary de la Notificacion
			$notification = array('title'=>$titulo , 'body' => $cuerpo);
			$datos = array('Titulo'=>$titulo, 'Fecha'=>$fecha, 'Lugar'=>$lugar);

			//Sumo el Token (Grupo) a la notificacion
			$arrayToSend = array('to' => $token, 'notification' => $notification, 'data' => $datos);

			//Generamos el JSON codificado.
			$json = json_encode($arrayToSend);

			//Configuramos los headers:
			$headers = array();
			$headers[] = "Content-Type: application/json";
			$headers[] = "Authorization: key=$serverKey";

			//Trabajamos el CURL
			curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST"); 
			curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
			curl_setopt($ch, CURLOPT_HTTPHEADER,$headers);

			echo "<p>Resulado de la Notificación:</p>";
			echo "<ul>";
			echo "<li>";

			//Enviamos la soliictud
			$resultado = curl_exec($ch);
			
			echo "</li>";
			echo "</ul>";			
			echo "[ <a href='notificacion.php'>Enviar otra notificación</a> ]";

			//Cerramos la solicitud
			curl_close($ch);
		}
		?>
 	<?php
 	}
 	else { 		
 		// Mostrar el formulario
 	?>
		<form class="bordes" action="<?php echo $_SERVER['PHP_SELF']; ?>" method="POST">
	 		<p>Datos para la Notificación:</p>
			<div class="grupos_campos">
				<span class="etiqueta">Titulo:</span>
				<input type="text" name="titulo" placeholder="Título de la notificación">
			</div>
			<div class="grupos_campos">
				<span class="etiqueta">Cuerpo:</span>
				<textarea name="cuerpo" cols="50" rows="5">Este es el texto que aparece debajo del titulo de la notificación</textarea>
			</div>
			<br>
	 		<p>Datos EXTRA para mostrar dentro de la App:</p>
			<div class="grupos_campos">
				<span class="etiqueta">Fecha:</span>
				<input type="text" name="fecha" placeholder="08-08-2020">
			</div>
			<div class="grupos_campos">
				<span class="etiqueta">Lugar:</span>
				<input type="text" name="lugar" placeholder="En algún lugar de este país">
			</div>
			<input type="submit" name="enviar" value="Enviar Notificación">
		</form>
	<?php
 	}
 	?>
</body>
</html>