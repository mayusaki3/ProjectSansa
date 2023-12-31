
#include <AzCore/Memory/SystemAllocator.h>
#include <AzCore/Module/Module.h>

#include "pongSystemComponent.h"

namespace pong
{
    class pongModule
        : public AZ::Module
    {
    public:
        AZ_RTTI(pongModule, "{8b31528d-b8de-4c5d-9706-1cc00fc921ec}", AZ::Module);
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
