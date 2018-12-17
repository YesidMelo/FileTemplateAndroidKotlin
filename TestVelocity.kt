// Asigna el nuevo valor 1
#set( $valor1 = -1)
// valor1
$valor1

//Captura Valor 2
$valor2

#if($valor1 > $valor2)
    $valor1 es mayor que $valor2
#elseif($valor1 == $valor2)
    $valor1 es igual que $valor2
#elseif($valor1 < $valor1)
    $valor1 es menor que $valor2
#else
    identifico el valor2
#end