
#include <AzCore/Memory/SystemAllocator.h>
#include <AzCore/Module/Module.h>

#include "pongSystemComponent.h"

namespace pong
{
    class pongModule
        : public AZ::Module
    {
    public:
        AZ_RTTI(pongModule, "{18a9140a-d371-45fd-ab8e-67b9aa4b443d}", AZ::Module);
        AZ_CLASS_ALLOCATOR(pongModule, AZ::SystemAllocator, 0);

        pongModule()
            : AZ::Module()
        {
            // Push results of [MyComponent]::CreateDescriptor() into m_descriptors here.
            m_descriptors.insert(m_descriptors.end(), {
                pongSystemComponent::CreateDescriptor(),
            });
        }

        /**
         * Add required SystemComponents to the SystemEntity.
         */
        AZ::ComponentTypeList GetRequiredSystemComponents() const override
        {
            return AZ::ComponentTypeList{
                azrtti_typeid<pongSystemComponent>(),
            };
        }
    };
}// namespace pong

AZ_DECLARE_MODULE_CLASS(Gem_pong, pong::pongModule)
