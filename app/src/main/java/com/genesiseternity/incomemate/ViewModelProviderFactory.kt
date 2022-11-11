package com.genesiseternity.incomemate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelProviderFactory @Inject
constructor(private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

/*
@Singleton
class ViewModelProviderFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

 */

/*
class ViewModelProviderFactory @Inject constructor(private val creators: Map<KClass<ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {
    //private var creators: Map<Class<? extends ViewModel>, Provider<ViewModel>>


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //val creator: Provider<KClass<ViewModel>> = creators.get(modelClass)
        val creator = creators.get(modelClass)



        return TODO()

        //return creators.get()
    }

    //val value: KClass<ViewModel>





    @Inject
    public ViewModelProviderFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators)
    {
        this.creators = creators
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        Provider<? extends ViewModel> creator = creators.get(modelClass)

        if (creator == null)
        {
            for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet())
            {
                if (modelClass.isAssignableFrom(entry.getKey()))
                {
                    creator = entry.getValue()
                    break
                }
            }
        }

        if (creator == null)
        {
            throw new IllegalArgumentException("Unknown modelClass" + modelClass)
        }

        try
        {
            return (T) creator.get()
        }
        catch (Exception e)
        {
            throw new RuntimeException(e)
        }
    }

}
     */
