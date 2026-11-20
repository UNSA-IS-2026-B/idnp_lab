
import asyncio
from pathlib import Path
import edge_tts

VOZ = "es-PE-CamilaNeural"
VELOCIDAD = "-5%"

# Directorio de recursos Android
DESTINO = Path(__file__).resolve().parent / "app/src/main/res/raw"
DESTINO.mkdir(parents=True, exist_ok=True)

AUDIOS = {
    "ingenieria_sistemas": """
    Bienvenidos a la Escuela Profesional de Ingeniería de Sistemas
    de la Universidad Nacional de San Agustín.
    Este pabellón está dedicado a la formación de profesionales
    especializados en tecnologías de la información y desarrollo
    de software. Cuenta con laboratorios de cómputo, espacios para
    el estudio de inteligencia artificial, redes y sistemas
    informáticos. Sus instalaciones favorecen la investigación,
    la innovación tecnológica y el desarrollo de proyectos
    que permiten aplicar los conocimientos adquiridos.
    """,

    "biblioteca_ingenierias": """
    Bienvenidos a la Biblioteca de Ingenierías de la Universidad
    Nacional de San Agustín. Este espacio académico ofrece salas
    de estudio individuales y grupales, bibliografía técnica
    especializada y acceso a recursos digitales.
    Su infraestructura facilita la consulta de información
    científica y el desarrollo de actividades de investigación.
    La biblioteca constituye un espacio importante para el
    aprendizaje y el desarrollo académico de la comunidad
    universitaria.
    """,

    "facultad_arquitectura": """
    Bienvenidos a la Facultad de Arquitectura de la Universidad
    Nacional de San Agustín. Este espacio está dedicado al diseño
    arquitectónico, la planificación urbana y la construcción
    de entornos sostenibles. Cuenta con talleres de diseño,
    dibujo técnico, maquetación y modelado arquitectónico.
    También dispone de ambientes para estudiar arquitectura
    bioclimática y representación de proyectos.
    Es un lugar donde la creatividad y la tecnología
    se combinan para formar futuros arquitectos.
    """,

    "ingenieria_civil": """
    Bienvenidos a la Escuela Profesional de Ingeniería Civil.
    Esta escuela forma profesionales dedicados al diseño,
    construcción y mantenimiento de infraestructura.
    Sus instalaciones incluyen laboratorios de mecánica
    de suelos, estructuras, hidráulica y topografía.
    Los estudiantes realizan ensayos de materiales,
    análisis de resistencia y prácticas de medición.
    Estos espacios permiten aplicar los conocimientos
    teóricos a problemas reales de ingeniería
    y desarrollar proyectos de construcción.
    """,

    "ingenieria_mecanica": """
    Bienvenidos a la Escuela Profesional de Ingeniería Mecánica.
    Aquí se desarrollan actividades relacionadas con
    la manufactura, el diseño de máquinas y los procesos térmicos.
    Sus instalaciones cuentan con talleres de soldadura,
    procesamiento de metales, motores y sistemas mecánicos.
    También se utilizan herramientas de diseño asistido
    por computadora para desarrollar proyectos tecnológicos.
    Estos ambientes permiten a los estudiantes aplicar
    principios de mecánica y mejorar sus competencias
    prácticas en ingeniería.
    """,

    "ingenieria_quimica": """
    Bienvenidos al área de Ingeniería Química y Metalúrgica.
    Este espacio académico está orientado al estudio
    de procesos químicos y transformaciones de materiales.
    Cuenta con laboratorios especializados en análisis,
    procesamiento de minerales y operaciones industriales.
    Los estudiantes desarrollan prácticas relacionadas
    con balances de materia, procesos térmicos
    y tratamiento de sustancias.
    La formación combina conocimientos científicos
    con aplicaciones tecnológicas de la industria
    química y metalúrgica.
    """,

    "produccion_servicios": """
    Bienvenidos a la Facultad de Ingeniería de Producción
    y Servicios de la Universidad Nacional de San Agustín.
    Este edificio alberga espacios académicos
    y administrativos de la facultad.
    Entre sus instalaciones se encuentran oficinas
    de coordinación, salas de docentes, ambientes
    de posgrado y auditorios.
    Aquí se desarrollan actividades de gestión,
    enseñanza y organización académica.
    La facultad contribuye a la formación
    de profesionales orientados a la innovación,
    la producción y los servicios.
    """,

    "matematica_fisica": """
    Bienvenidos a la Escuela de Matemática y Física.
    Este espacio está dedicado al estudio de las ciencias
    fundamentales que sustentan distintas disciplinas
    de la ingeniería y la investigación.
    Cuenta con aulas teóricas y laboratorios
    para el aprendizaje de física experimental,
    óptica y otras áreas científicas.
    Los estudiantes desarrollan conocimientos
    de cálculo, análisis matemático y fenómenos físicos.
    Sus instalaciones contribuyen al desarrollo
    del pensamiento científico y la investigación.
    """,

    "pabellon_nicholson": """
    Bienvenidos al Pabellón Nicholson, también conocido
    como Pabellón de la Cultura.
    Este edificio constituye un espacio importante
    para las actividades culturales y académicas
    de la Universidad Nacional de San Agustín.
    Su infraestructura alberga conferencias magistrales,
    ceremonias, congresos y encuentros universitarios.
    Además, posee valor arquitectónico y patrimonial.
    Es un lugar que promueve el intercambio de conocimientos,
    la participación de la comunidad universitaria
    y la difusión de actividades culturales.
    """,

    "estadio_ho_chi_minh": """
    Bienvenidos al Estadio Ho Chi Minh de la Universidad
    Nacional de San Agustín.
    Este escenario deportivo cuenta con una cancha
    de fútbol, pista atlética y tribunas.
    Sus instalaciones se utilizan para entrenamientos,
    competencias deportivas y torneos entre escuelas.
    También favorecen la preparación física
    de las selecciones universitarias.
    El estadio representa un espacio de integración,
    recreación y desarrollo deportivo,
    promoviendo hábitos saludables
    entre los estudiantes de la universidad.
    """,

    "infounsa": """
    Bienvenidos a Infounsa, el centro de informática
    y soporte tecnológico de la Universidad Nacional
    de San Agustín.
    Este espacio se encuentra relacionado
    con la administración de redes, servicios informáticos
    e infraestructura tecnológica universitaria.
    Sus actividades incluyen soporte técnico,
    gestión de servidores y capacitación digital.
    Infounsa contribuye al funcionamiento
    de los servicios tecnológicos necesarios
    para las actividades académicas
    y administrativas de la institución.
    """,

    "panificadora_imprenta": """
    Bienvenidos a la Panificadora e Imprenta Universitaria.
    Estas instalaciones forman parte de los servicios
    de producción de la Universidad Nacional de San Agustín.
    La panificadora desarrolla actividades relacionadas
    con la elaboración de productos de panadería
    destinados al abastecimiento universitario.
    Por su parte, la imprenta proporciona servicios
    de impresión de materiales académicos
    e institucionales.
    Ambas unidades contribuyen al funcionamiento
    de distintos servicios de la universidad.
    """,

    "ingenieria_geologica": """
    Bienvenidos al área de Ingeniería Geológica.
    Este espacio está dedicado al estudio de la Tierra,
    sus materiales y los procesos geológicos.
    Cuenta con laboratorios relacionados con
    petrología, mineralogía y geotecnia.
    Los estudiantes analizan muestras de rocas,
    interpretan mapas geológicos
    y desarrollan actividades de investigación.
    Estas instalaciones permiten comprender
    la composición del terreno y aplicar
    conocimientos geológicos a problemas
    de exploración y obras de ingeniería.
    """,

    "escuela_arte": """
    Bienvenidos a la Escuela de Arte de la Universidad
    Nacional de San Agustín.
    Este espacio académico promueve la creatividad,
    la expresión artística y el desarrollo cultural.
    Cuenta con talleres destinados a disciplinas
    como pintura, escultura y artes plásticas.
    Los estudiantes realizan actividades
    de experimentación, creación y exposición
    de obras artísticas.
    La escuela contribuye a fortalecer
    la cultura y la participación artística
    dentro de la comunidad universitaria.
    """
}


async def generar_audio(nombre, texto, semaforo):
    async with semaforo:
        archivo = DESTINO / f"{nombre}.mp3"

        try:
            comunicacion = edge_tts.Communicate(
                text=" ".join(texto.split()),
                voice=VOZ,
                rate=VELOCIDAD
            )

            await comunicacion.save(str(archivo))

            if archivo.exists() and archivo.stat().st_size > 0:
                print(f"[OK] {archivo.name}")
            else:
                print(f"[ERROR] Archivo vacío: {archivo.name}")

        except Exception as error:
            print(f"[ERROR] {nombre}: {error}")


async def main():
    print("Generando las 14 audio-guías del campus...\n")

    # Limitar solicitudes simultáneas al servicio TTS
    semaforo = asyncio.Semaphore(2)

    await asyncio.gather(*[
        generar_audio(nombre, texto, semaforo)
        for nombre, texto in AUDIOS.items()
    ])

    generados = [
        nombre for nombre in AUDIOS
        if (DESTINO / f"{nombre}.mp3").exists()
        and (DESTINO / f"{nombre}.mp3").stat().st_size > 0
    ]

    print(f"\nAudios generados: {len(generados)}/{len(AUDIOS)}")
    print(f"Ubicación: {DESTINO}")


if __name__ == "__main__":
    asyncio.run(main())
