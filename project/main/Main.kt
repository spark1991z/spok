package project.main

/**
 * Класс Main
 */
class Main private constructor() {

    /**
     * Переопределение функции toString
     * Возвращает строку с названием проекта и версии
     */
    override fun toString(): String {
        return "$PROJECT_NAME $VERSION_CORE.$VERSION_CODE-$VERSION_BUILD #$VERSION_INCREMENT ($SYSTEM_OS_NAME, $SYSTEM_OS_ARCH)"
    }

    /**
     * Определение константов и функций класса
     */
    companion object {

        private var main:Main ?= null

        private var PROJECT_NAME:String ?= null
        private var VERSION_CORE:Double ?= null
        private var VERSION_CODE:Int ?= null
        private var VERSION_BUILD:Double ?= null
        private var VERSION_INCREMENT:Int ?= null
        private var RZD = "---------------------------"
        private var SYSTEM_OS_NAME:String ?= null
        private var SYSTEM_OS_ARCH:String ?= null

        init {
            PROJECT_NAME = "Spok"
            VERSION_CORE = 0.1
            VERSION_CODE = 7
            VERSION_BUILD = 29.0
            VERSION_INCREMENT = 4
            SYSTEM_OS_NAME = System.getProperty("os.name")
            SYSTEM_OS_ARCH = System.getProperty("os.arch")
        }


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
        fun start(){
           println("Starting services...")
        }

        /**
         * Функция stop
         * Остановка сервисов
         */
        fun stop(){
            println("Stoping services...")
        }

        /**
         * Функция restart
         * Перезапуск сервисов
         */
        fun restart(){
            println("Restarting...")
            stop()
            start()
        }

        /**
         * Функция shutdown
         * Остановка сервисов и завершение выполнения программы
         */
        fun shutdown(){
            stop()
            println("Program will be shutdowned.\n" +
                    "Bye.")
            System.exit(0)
        }
    }
}

