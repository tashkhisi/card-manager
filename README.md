# card-manager
من در ابتدا الگوریتم مورد نظر برای حل مسئله رو توضیح میدم و بعد از اون روش هایی که برای موازی کردن الگوریتم پیش رو هست رو بررسی می کنم  و کلاس  های که در پروژه هر کدام از این روش ها رو پیاده سازی کردن رو بررسی می کنم . و نتیجه تست بار مربوط به هرکدام ائنها رو شرح میدم و در آخر هم  روشی که برای تست موازی الگوریتم ها استفاده کردم رو توضیح میدم ولی با توجه به زمانی که داشتم امکان نوشتن تست با code coverage بالا وجود نداشته ولی سعی کردم از انواع تست توی برنامه استفاده کنم و از کتابخانه هایی که تست موازی رو ساده تر می کنند(مثل MulthithreadedTC) و نیاز به استفاده از سینکرونایزرهای مثل CountDownLatch رو کمتر می کنند  استفاده کنم. همچمنین تست ها با دیتاست متغیر طراحی نشدن و داده  های تستی درون کلاس های تست هاردکد شدن ولی امکان ایجاد دیتاست وجود داره.
خوب در ایتدا هدف ما مینیموم کردن تعداد کارت ها است. من اینجا از یک الگوریتم حریصانه استفاده می کنم، برای اینکه تعداد کارت ها برای مبلغ انتفال درخواستی کاربر( که اسم اون رو amount میزاریم) همیشه مینیموم بشه مراحل زیر رو انجام میدیم
1.	عنصری رو به نام عنصر سقف مبلغ (یا همون (ceiling از مجموعه کارت ها انتخاب می کنم که کوچکترین کارت در مجموعه کارت هاست که موجودی قابل انتقال اون (در برنامه اسم این متغیر transferable_amount هست هرچند که قبول دارم شاید اسم خوبی نباشه) از مبلغ درخواستی کاربر بیشتر است. اگر چنین کارتی در مجموعه کارت ها وجود داشته باشه انتقال میتونه با یک کارت انجام بشه  و کار الگوریتم اینجا به پایان میرسه.
2.	 اما اگر چنین عنصری در مجموعه کارت های وجود نداشته باشه (به این معنی که transferable_amount برای همه کارت ها بیشتر از مبلغ درخواستی کاربر باشه) برای رسیدن به هدف مینیموم شدن تعداد کارت ها عنصر با بیشترین مبلغ رو از مجموعه کارت ها انتخاب می کنیم که اسم این عنصر رو first میزاریم. بعد از انتخاب این عنصر باید مبلغ مورد درخواست کاربر رو از این مبلغ کم کنیم تا مبلغ باقی مانده برای اجرای کامل تراکنش کاربر بدست بیاد. بنابراین مبلغ جدیدی که در این مرحله داریم برابر خواهد بود با card.transferable_amount – amount  و در واقع این مبلغ جدیدی هست که الگوریتم به صورت بازگشتی دوباره به ازای اون اجرا میشه (هرچند در برنامه از حلقه while   استفاده شده ولی می توانیم اون رو به صورت بازگشتی بازنویسی کنیم)
3.	شرط توقف الگوریتم : در هر مرحله مقداری که از کارت های مورد نظر کم می کنیم رو با هم جمع می کنیم که این مفدار در واقع تراکنش کاربر رو تشکلیل میدهد.
نکته مهمی که باید به اون توجه داشت این است که بعد از کسر مبلغ از کارت مورد نظر در صورتی که مقدار موجودی اون بیشتر از 10000  تومان باشه اون کارت باید به دسته کارت  ها اضافه بشه و در غیر این صورت اون کارت دیگه به دسته کارت ها اضافه نمیشه.
بعد از انتخاب تراکنش ها ما در همین لحظه یک موجودیت با نام transactionGroup که شامل یک شناسه(Id) و لیست تراکنش های انتخاب شده است رو به کاربر برمی گردانیم (در این مرحله وضعیت همه این تراکنش ها pending  و یا معلق است) و از طرف دیگر به صورت موازی بخش اجرای تراکنش که معمولا با endpoint دیگری انجام میشه رو بر روی executorService با thread های مجزا اجرا می کنم (که به صورت async اجرا میشه)که این دو بخش سیستم که مریوط به اجرای تراکنش و انتخاب تراکنش ها هست رو از هم جدا میکنه. در واقع این به نوعی پیاده سازی bulkhead pattern هست.
بعد از اجرای تراکنشها و ارتباط با endpoint مورد نظر من در پیاده سازی خودم listenerی رو دارم که توسط thread هایی که  تراکنش رو اجرا می کنند فراخوانی میشه این listener  سه متد برای اطلاع از موفقیت، شکست تراکنش و همچنین اطلاع از intrupt threadی  که تراکنش بر روی اون اجرا میشده رو پیاده سازی می کند. در نیازمندی ما اگر تراکنش مورد نظر موفق باشد فقط لازم است وضعیت اون تراکنش رو در پایگاه داده به روز رسانی کنیم ولی اگر تراکنش مورد نظر ناموفق باشد نیاز است مقدار مبلغ اون تراکنش رو به کارت مورد نظر اضافه کنیم و وضعیت اون تراکنش رو در پایگاه داده ناموفق ثیت کنیم. از طرف دیگه اگر thread مربوط به اون تراکنش intrrupt شده باشد  وضعیت اون تراکنش در پایگها داده به مغایرت تغییر خواهد کرد تا در آینده وضعیت آن به صورت دستی بررسی شود.
در پیاده سازی بخش انتخاب کارت ما قطعا به ساختمان داده ای نیاز داریم که بتوانیم جست و جو وinsert   کارت رو بر اساس مبلغ
با سریعترین زمان انجام بدیم  که قطعا این ساختمان داده درخت است که پیاده سازی JRE اونها  TreeMap   و ConcurrentSkipListMap هستند.  تنها تفاوت ساختار مورد نیاز ما با درخت در این است که در ساختار ما چندین عنصر بر روی بک نود از درخت قرار خواهند گرفت. یعنی مثلا برای مبلغ 3000000  تومان ممکن است 100000  کارت وجود داشته باشد.  برای حل این مشکل ما برای value مربوط به هرnode  از درخت یک map در نظر میگیریم که کارت هایی که آن مبلغ را دارا هستند در آن Map قرارمی گیرند.برای انتخاب بین treeMap  و  ConcurrentSkipListMap  باید در نظر بگیریم که treeMap  قابلیت استفاده موازی را ندارد و Thread safe نیست و در صورت استفاده از این ساختار نیاز به lock بخش انتخاب تراکنش ها به صورت کامل وجود دارد زیرا در غیر این صورت ساختار map در هم خواهد شکست. concurrentSkipListMap به صورت lock free پیاده سازی شده و کاملا thread safe هست. در برنامه هر دو پیاده سازی یکی با نام TreeMapTransactionManager و دیگری به نام ConCurrentTransactionManager وجود دارد.
پیاده سازی مریوط به TreeMap سرراست است برای این کار کل قسمت مربوط به انتخاب تراکنش ها را lock می کنیم.
اما پیاده سازی مربوط به ConcurrentSkipListMap پیچیدگی هایی دارد ولی نتیجه تست نشان میدهد ارزش پیاده سازی را داشته است.
