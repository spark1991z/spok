package project.main
/**
 * Small Project On Kotlin
 * Небольшой Проект На Котлине
 *
 * Класс Main
 */
class Main private constructor() {

    /**
     * Переопределение функции toString
     * Возвращает строку с названием проекта и версии
     */
    override fun toString(): String {
        return "$PROJECT_NAME (v$VERSION_CORE.$VERSION_CODE, build $VERSION_BUILD)"
    }

    /**
     * Определение константов и функций класса
     */
    companion object {

        private var main: Main? = null

        val PROJECT_NAME = "Spok"
        val VERSION_CORE = 0.1
        val VERSION_CODE = 0.1
        val VERSION_BUILD = 2.1
        val RZD = "---------------------------"

        /**
         * Функция main
         *
         * @param args - Аргументы приложения в виде массива строк
         */
        @JvmStatic fun main(args: Array<String>) {
            if (main != null) return
            main = Main()
            println("$RZD\n$main\n$RZD")
            start()
        }

        /**
         * Функция start
         * Запуск сервисов
         */
        @JvmStatic fun start(){
           println("Starting services...")
        }

        /**
         * Функция stop
         * Остановка сервисов
         */
        @JvmStatic fun stop(){
            println("Stoping services...")
        }

        /**
         * Функция restart
         * Перезапуск сервисов
         */
        @JvmStatic fun restart(){
            println("Restarting...")
            stop()
            start()
        }

        /**
         * Функция shutdown
         * Остановка сервисов и завершение выполнения программы
         */
        @JvmStatic fun shutdown(){
            stop()
            println("Program will be shutdowned..\n" +
                    "Bye.")
            System.exit(0)
        }
    }
}


