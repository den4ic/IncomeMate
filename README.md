# IncomeMate
Приложение для учета финансов и ведения бюджета.
___
Функциональность приложения представлена на диаграмме вариантов использования UML, разноцветные прецеденты указывают на недоработанную реализацию.

<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/UseCaseDiagramUML.png" width="768">

___

В качестве стека, задействаованы:
+ **Dagger 2** - для внедрения зависимостей;
+ Для хранения данных используется библиотека **Room**;
+ Многопоточность реализована посредством **RxJava 3**;
+ UI приложения состоит из: **ViewPager2, RecyclerView, Custom view, Lottie**;
+ Регистарция пользователя основана на сервисе **Firebase**;
+ Для обнаружения утечек памяти задействаован **LeakCanary**;
+ При работе с сетью используется **Retrofit** для парсинга валют с последующей конвертацией.

Скриншоты экранов приложения представлены ниже.

<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_231537.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_231600.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_231628.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_231700.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_232204.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_232511.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_232521.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20221216_232551.png" width="256"> |
<img src="https://github.com/den4ic/IncomeMate/blob/main/screen/Screenshot_20230103_201855.png" width="256">
